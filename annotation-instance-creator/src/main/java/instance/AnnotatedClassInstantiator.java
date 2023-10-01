package instance;

import instance.ObjectGraph.ReadOnlyObjectGraph;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AnnotatedClassInstantiator {
    private final Annotations instantiateAnnotations;

    public AnnotatedClassInstantiator(Annotations instantiateAnnotations) {
        Objects.requireNonNull(instantiateAnnotations);
        this.instantiateAnnotations = instantiateAnnotations;
    }

    public ReadOnlyObjectGraph load(Class<?> clazz, ReadOnlyObjectGraph prevObjectGraph) {
        if (Objects.isNull(clazz)) {
            throw new RuntimeException("does not exist load clazz.");
        }
        if (Objects.isNull(prevObjectGraph)) {
            prevObjectGraph = ReadOnlyObjectGraph.empty();
        }

        boolean doesNotExistMatchTargetAnnotation = instantiateAnnotations.noneAnnotatedFrom(clazz);
        if (doesNotExistMatchTargetAnnotation) {
            return prevObjectGraph;
        }

        ObjectGraph objectGraph = ObjectGraph.empty();
        Set<Class<?>> alreadyVisitedClasses = new LinkedHashSet<>();
        Object instantiate = instantiate(clazz, objectGraph, prevObjectGraph, alreadyVisitedClasses);

        objectGraph.put(clazz, instantiate);

        ReadOnlyObjectGraph newReadOnlyObjectGraph = objectGraph.lock();
        return prevObjectGraph.merge(newReadOnlyObjectGraph);
    }

    private Object instantiate(Class<?> clazz, ObjectGraph newObjectGraph, ReadOnlyObjectGraph prevObjectGraph, Set<Class<?>> alreadyVisitedClasses) {
        if (alreadyVisitedClasses.contains(clazz)) {
            String alreadyVisitedClassesName = alreadyVisitedClasses.stream()
                .map(Class::getSimpleName)
                .collect(Collectors.joining(",", "[", "]"));
            log.info("already visited class. alreadyVisited class : `{}`, current class : `{}`", alreadyVisitedClassesName, clazz.getSimpleName());
            throw new RuntimeException("already visited class. alreadyVisited class : " + alreadyVisitedClassesName);
        }
        alreadyVisitedClasses.add(clazz);

        if (newObjectGraph.containsKey(clazz) || prevObjectGraph.containsKey(clazz)) {
            alreadyVisitedClasses.remove(clazz);
            return newObjectGraph.containsKey(clazz) ? newObjectGraph.get(clazz) : prevObjectGraph.get(clazz);
        }

        Object instance = doInstantiate(clazz, newObjectGraph, prevObjectGraph, alreadyVisitedClasses);
        newObjectGraph.put(clazz, instance);

        // need remove. ex) s1, s2 ref r1
        // s1, r1 already has instance.
        // s2 try instantiate r1 already visited. could throw exception.
        alreadyVisitedClasses.remove(clazz);

        return instance;
    }

    private Object doInstantiate(Class<?> clazz, ObjectGraph newObjectGraph, ReadOnlyObjectGraph prevObjectGraph, Set<Class<?>> alreadyVisitedClasses) {
        Set<Class<?>> annotatedFields = instantiateAnnotations.peekAnnotatedFieldsFrom(clazz);
        Constructor<?> foundConstructor = findConstructorByContainAllFields(clazz, annotatedFields);

        Object[] memberObjects = Arrays.stream(foundConstructor.getParameters())
            .map(Parameter::getType)
            .map(constructorParam -> this.instantiate(constructorParam, newObjectGraph, prevObjectGraph, alreadyVisitedClasses))
            .toArray(Object[]::new);

        return newObject(foundConstructor, memberObjects);
    }

    private static Constructor<?> findConstructorByContainAllFields(Class<?> clazz, Set<Class<?>> fields) {
        return Arrays.stream(clazz.getConstructors())
            .filter(constructor -> isContainAllFields(constructor, fields))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Does not exist match constructor."));
    }

    private static boolean isContainAllFields(Constructor<?> constructor, Set<Class<?>> memberClazzes) {
        return Arrays.stream(constructor.getParameters())
            .map(Parameter::getType)
            .allMatch(memberClazzes::contains);
    }

    private static Object newObject(Constructor<?> constructor, Object[] memberObjects) {
        try {
            return constructor.newInstance(memberObjects);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
