package instance;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AnnotatedClassInstantiator {
    private final ObjectRepository objectRepository;
    private final Annotations instantiateAnnotations;

    public AnnotatedClassInstantiator(ObjectRepository objectRepository, Annotations instantiateAnnotations) {
        Objects.requireNonNull(objectRepository);
        Objects.requireNonNull(instantiateAnnotations);

        this.objectRepository = objectRepository;
        this.instantiateAnnotations = instantiateAnnotations;
    }

    public ObjectRepository load(Class<?> clazz) {
        boolean doesNotExistMatchTargetAnnotation = instantiateAnnotations.noneAnnotatedFrom(clazz);
        if (doesNotExistMatchTargetAnnotation) {
            return objectRepository;
        }

        ObjectRepository newObjectRepository = ObjectRepository.empty();
        Set<Class<?>> alreadyVisitedClasses = new LinkedHashSet<>();
        Object instantiate = instantiate(clazz, newObjectRepository, objectRepository, alreadyVisitedClasses);
        newObjectRepository.put(clazz, instantiate);
        return newObjectRepository;
    }

    private Object instantiate(Class<?> clazz, ObjectRepository newObjectRepository, ObjectRepository prevObjectRepository, Set<Class<?>> alreadyVisitedClasses) {
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

    private Object doInstantiate(Class<?> clazz, ObjectRepository newObjectRepository, ObjectRepository prevObjectRepository, Set<Class<?>> alreadyVisitedClasses) {
        Class<?>[] memberClazzes = instantiateAnnotations.peekAnnotatedFieldsFrom(clazz).toArray(Class<?>[]::new);
        Object[] memberObjects = Arrays.stream(memberClazzes)
            .map(memberClazz -> this.instantiate(memberClazz, newObjectRepository, prevObjectRepository, alreadyVisitedClasses))
            .toArray();
        return newObject(clazz, memberClazzes, memberObjects);
    }

    private static Object newObject(Class<?> clazz, Class<?>[] memberClasses, Object[] memberObjects) {
        try {
            Constructor<?> constructor = clazz.getConstructor(memberClasses);
            return constructor.newInstance(memberObjects);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
