package beanContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import mapper.AnnotationUtils;
import marker.Component;

@Slf4j
public class ContainerCreator {

    public static Map<Class<?>, Object> create(List<Class<?>> classes) {
        List<Class<?>> componentClasses = classes.stream()
            .filter(clazz -> AnnotationUtils.exist(clazz, Component.class))
            .collect(Collectors.toUnmodifiableList());

        List<ComponentClassLoader> componentClassLoaders = componentClasses.stream()
            .map(ComponentClassLoader::new)
            .collect(Collectors.toUnmodifiableList());

        Map<Class<?>, Object> container = new HashMap<>();
        for (ComponentClassLoader classLoader : componentClassLoaders) {
            Map<Class<?>, Object> newContainer = classLoader.load(container);
            newContainer.forEach((key, value) -> container.merge(key, value, (prev, curr) -> prev));
        }
        container.forEach((key, value) -> log.info("class : `{}`, obj : `{}`", key, value));

        return container;
    }
}
