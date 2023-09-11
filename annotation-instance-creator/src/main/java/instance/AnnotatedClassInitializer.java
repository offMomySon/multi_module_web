package instance;

import com.main.util.AnnotationUtils;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AnnotatedClassInitializer {
    private final ObjectRepository prevObjectRepository;
    private final Set<Class<?>> targetAnnotations;

    public AnnotatedClassInitializer(ObjectRepository prevObjectRepository, Set<Class<?>> targetAnnotations) {
        Objects.requireNonNull(prevObjectRepository);
        Objects.requireNonNull(targetAnnotations);

        targetAnnotations = targetAnnotations.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toUnmodifiableSet());
        if (targetAnnotations.isEmpty()) {
            throw new RuntimeException("does not exist targetAnnotation");
        }
        this.prevObjectRepository = prevObjectRepository;
        this.targetAnnotations = targetAnnotations;
    }

    public ObjectRepository load(Class<?> clazz) {
        boolean doesNotExistMatchTargetAnnotation = targetAnnotations.stream()
            .noneMatch(targetAnnotation -> AnnotationUtils.exist(clazz, targetAnnotation));
        if (doesNotExistMatchTargetAnnotation) {
            return prevObjectRepository;
        }

        ObjectRepository newObjectRepository = ObjectRepository.empty();
        Object instantiate = instantiate(clazz, newObjectRepository, prevObjectRepository, new LinkedHashSet<>());
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
        Class<?>[] memberClazzes = targetAnnotations.stream()
            .map(targetAnnotation -> AnnotationUtils.peekFieldsType(clazz, targetAnnotation))
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableSet())
            .toArray(Class<?>[]::new);

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
