package beanContainer;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import mapper.AnnotationUtils;
import marker.Component;

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

    public Map<Class<?>, Object> load(Map<Class<?>, Object> container) {
        try{
            Object instantiate = instantiate(clazz, container, new LinkedHashSet<>());
            container.put(clazz, instantiate);
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        return container;
    }

    private Object instantiate(Class<?> clazz, Map<Class<?>, Object> container, Set<Class<?>> alreadyVisitedClasses) throws Exception {
        if (alreadyVisitedClasses.contains(clazz)) {
            String alreadyVisitedClassesName = alreadyVisitedClasses.stream()
                .map(Class::getSimpleName)
                .collect(Collectors.joining(",", "[", "]"));
            log.info("already visited class. alreadyVisited class : `{}`, current class : `{}`", alreadyVisitedClassesName, clazz.getSimpleName());
            throw new RuntimeException("already visited class. alreadyVisited class : " + alreadyVisitedClassesName);
        }
        alreadyVisitedClasses.add(clazz);

        if (container.containsKey(clazz)) {
            alreadyVisitedClasses.remove(clazz);
            return container.get(clazz);
        }

        Object instance = doInstantiate(clazz, container, alreadyVisitedClasses);

        container.put(clazz, instance);

        // need remove. ex) s1, s2 ref r1
        // s1, r1 already has instance.
        // s2 try instantiate r1 already visited. could throw exception.
        alreadyVisitedClasses.remove(clazz);

        return instance;
    }

    private Object doInstantiate(Class<?> clazz, Map<Class<?>, Object> container, Set<Class<?>> alreadyVisitedClasses) throws Exception {
        Class<?>[] memberClasses = AnnotationUtils.peekFieldsType(clazz, COMPONENT_CLASS).toArray(Class<?>[]::new);
        for (Class<?> memberClazz : memberClasses) {
            this.instantiate(memberClazz, container, alreadyVisitedClasses);
        }

        Object[] memberObjects = Arrays.stream(memberClasses).map(container::get).toArray();
        Constructor<?> constructor = clazz.getConstructor(memberClasses);
        return constructor.newInstance(memberObjects);
    }
}
