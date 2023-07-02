package container;

import container.annotation.Component;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import util.AnnotationUtils;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ComponentClassLoader {
    private static final Class<Component> COMPONENT_CLASS = Component.class;

    private final Class<?> clazz;

    public ComponentClassLoader(@NonNull Class<?> clazz) {
        if (AnnotationUtils.doesNotExist(clazz, COMPONENT_CLASS)) {
            throw new RuntimeException("does not exist component annotation");
        }
        this.clazz = clazz;
    }

    public ObjectRepository load(ObjectRepository prevObjectRepository) {
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
        Class<?>[] memberClasses = AnnotationUtils.peekFieldsType(clazz, COMPONENT_CLASS).toArray(Class<?>[]::new);

        Object[] memberObjects = Arrays.stream(memberClasses)
                .map(memberClazz -> this.instantiate(memberClazz, newObjectRepository, prevObjectRepository, alreadyVisitedClasses))
                .toArray();

        return newObject(clazz, memberClasses, memberObjects);
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
