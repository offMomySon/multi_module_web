package beanContainer;

import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import mapper.AnnotationUtils;
import mapper.marker.Component;

@Slf4j
public class BeanContainerCreator {

    public static Map<Class<?>, Object> create(List<Class<?>> classes) {
        Map<Class<?>, Object> container = new HashMap<>();

        List<Class<?>> componentClasses = classes.stream()
            .filter(c -> AnnotationUtils.exist(c, Component.class))
            .collect(Collectors.toUnmodifiableList());

        for (Class<?> componentClazz : componentClasses) {
            container = instantiate(componentClazz, container, new HashSet<>());
        }

        return container;
    }

    private static Map<Class<?>, Object> instantiate(Class<?> clazz, Map<Class<?>, Object> container, Set<Class<?>> parentClasses) {
        try {
            List<Class<?>> instanceMemberClasses = Arrays.stream(clazz.getDeclaredFields())
                .map(f -> f.getType())
                .filter(c -> AnnotationUtils.exist(c, Component.class))
                .collect(Collectors.toUnmodifiableList());

            //  class 의 instance member class 가 존재하지 않으면 instance 를 생성한다.
            if (instanceMemberClasses.isEmpty()) {
                Constructor<?> constructor = clazz.getConstructor();
                Object instance = constructor.newInstance();
                container.put(clazz, instance);
                return container;
            }

            parentClasses.add(clazz);

            List<Class<?>> circularReferenceClass = instanceMemberClasses.stream()
                .filter(parentClasses::contains)
                .collect(Collectors.toUnmodifiableList());

            boolean hasCircularReferenceClass = !circularReferenceClass.isEmpty();
            if (hasCircularReferenceClass) {
                String recursiveClassNames = circularReferenceClass.stream()
                    .map(Class::getSimpleName)
                    .collect(Collectors.joining(",", "[", "]"));

                throw new RuntimeException(MessageFormat.format("current class : `{0}`, recursiveClasses : `{1}`", clazz.getSimpleName(), recursiveClassNames));
            }

            // class 의 모든 instance member class 의 instance 를 생성한다.
            for (Class<?> instanceMemberClazz : instanceMemberClasses) {
                container = instantiate(instanceMemberClazz, container, parentClasses);
            }

            // class 의 instance 를 생성한다.
            Object[] instanceMemberObjects = instanceMemberClasses.stream().map(container::get).toArray();
            Constructor<?> constructor = clazz.getConstructor(instanceMemberClasses.toArray(Class<?>[]::new));
            Object instance = constructor.newInstance(instanceMemberObjects);

            container.put(clazz, instance);

            parentClasses.remove(clazz);

            return container;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

















