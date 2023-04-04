package beanContainer;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import mapper.AnnotationUtils;
import marker.Component;

@Slf4j
public class ContainerCreator {
    private static final Class<Component> COMPONENT_CLASS = Component.class;

    private final List<Class<?>> componentClasses;

    public ContainerCreator(List<Class<?>> classes) {
        Objects.requireNonNull(classes, "classes is null.");

        this.componentClasses = classes.stream()
            .filter(clazz -> !Objects.isNull(clazz))
            .filter(clazz -> AnnotationUtils.exist(clazz, COMPONENT_CLASS))
            .collect(Collectors.toUnmodifiableList());
    }

    public BeanContainer create() {
        List<ComponentClassLoader> componentClassLoaders = componentClasses.stream()
            .map(ComponentClassLoader::new)
            .collect(Collectors.toUnmodifiableList());

        BeanContainer container = new BeanContainer();
        for (ComponentClassLoader classLoader : componentClassLoaders) {
            BeanContainer newContainer = classLoader.load(container);
            container.merge(newContainer);
        }

        return container;
    }
}
