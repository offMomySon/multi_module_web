package beanContainer;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import mapper.AnnotationUtils;
import marker.Component;

@Slf4j
public class ComponentContainerCreator {
    private static final Class<Component> COMPONENT_CLASS = Component.class;

    private final List<Class<?>> componentClasses;

    public ComponentContainerCreator(List<Class<?>> classes) {
        Objects.requireNonNull(classes, "classes is null.");

        this.componentClasses = classes.stream()
            .filter(clazz -> !Objects.isNull(clazz))
            .filter(clazz -> AnnotationUtils.exist(clazz, COMPONENT_CLASS))
            .collect(Collectors.toUnmodifiableList());
    }

    public ComponentContainer create() {
        List<ComponentClassLoader> componentClassLoaders = componentClasses.stream()
            .map(ComponentClassLoader::new)
            .collect(Collectors.toUnmodifiableList());

        ComponentContainer container = new ComponentContainer();
        for (ComponentClassLoader classLoader : componentClassLoaders) {
            ComponentContainer newContainer = classLoader.load(container);
            container.merge(newContainer);
        }

        return container;
    }
}
