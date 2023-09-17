package instance;

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

    public ReadOnlyObjectRepository load(Class<?> clazz, ReadOnlyObjectRepository prevObjectRepository) {
        if (Objects.isNull(clazz)) {
            throw new RuntimeException("does not exist load clazz.");
        }
        if (Objects.isNull(prevObjectRepository)) {
            prevObjectRepository = ReadOnlyObjectRepository.empty();
        }

        boolean doesNotExistMatchTargetAnnotation = instantiateAnnotations.noneAnnotatedFrom(clazz);
        if (doesNotExistMatchTargetAnnotation) {
            return prevObjectRepository;
        }

        ObjectRepository objectRepository = ObjectRepository.empty();
        Set<Class<?>> alreadyVisitedClasses = new LinkedHashSet<>();
        Object instantiate = instantiate(clazz, objectRepository, prevObjectRepository, alreadyVisitedClasses);
        objectRepository.put(clazz, instantiate);

        ReadOnlyObjectRepository newReadOnlyRepository = objectRepository.lock();
        return prevObjectRepository.merge(newReadOnlyRepository);
    }

    private Object instantiate(Class<?> clazz, ObjectRepository newObjectRepository, ReadOnlyObjectRepository prevObjectRepository, Set<Class<?>> alreadyVisitedClasses) {
        if (alreadyVisitedClasses.contains(clazz)) {
            String alreadyVisitedClassesName = alreadyVisitedClasses.stream()
                .map(Class::getSimpleName)
                .collect(Collectors.joining(",", "[", "]"));
            log.info("already visited class. alreadyVisited class : `{}`, current class : `{}`", alreadyVisitedClassesName, clazz.getSimpleName());
            throw new RuntimeException("already visited class. alreadyVisited class : " + alreadyVisitedClassesName);
        }
        alreadyVisitedClasses.add(clazz);

        if (newObjectRepository.containsKey(clazz) || prevObjectRepository.containsKey(clazz)) {
            alreadyVisitedClasses.remove(clazz);
            return newObjectRepository.containsKey(clazz) ? newObjectRepository.get(clazz) : prevObjectRepository.get(clazz);
        }

        Object instance = doInstantiate(clazz, newObjectRepository, prevObjectRepository, alreadyVisitedClasses);
        newObjectRepository.put(clazz, instance);

        // need remove. ex) s1, s2 ref r1
        // s1, r1 already has instance.
        // s2 try instantiate r1 already visited. could throw exception.
        alreadyVisitedClasses.remove(clazz);

        return instance;
    }

    private Object doInstantiate(Class<?> clazz, ObjectRepository newObjectRepository, ReadOnlyObjectRepository prevObjectRepository, Set<Class<?>> alreadyVisitedClasses) {
        Set<Class<?>> memberClazzes = instantiateAnnotations.peekAnnotatedFieldsFrom(clazz);
        Constructor<?> foundConstructor = findConstructorByAllContainClazzes(clazz, memberClazzes);

        Object[] memberObjects = Arrays.stream(foundConstructor.getParameters())
            .map(Parameter::getType)
            .map(constructorParam -> this.instantiate(constructorParam, newObjectRepository, prevObjectRepository, alreadyVisitedClasses))
            .toArray(Object[]::new);

        return newObject(foundConstructor, memberObjects);
    }

    private static Constructor<?> findConstructorByAllContainClazzes(Class<?> clazz, Set<Class<?>> memberClazzes) {
        return Arrays.stream(clazz.getConstructors())
            .filter(constructor -> isAllContainClazzesAsParams(constructor, memberClazzes))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Does not exist match constructor."));
    }

    private static boolean isAllContainClazzesAsParams(Constructor<?> constructor, Set<Class<?>> memberClazzes) {
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
