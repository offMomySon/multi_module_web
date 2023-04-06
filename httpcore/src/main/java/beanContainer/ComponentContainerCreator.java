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

    public Container create() {
        List<ComponentClassLoader> componentClassLoaders = componentClasses.stream()
            .map(ComponentClassLoader::new)
            .collect(Collectors.toUnmodifiableList());

        Container container = new Container();
        for (ComponentClassLoader classLoader : componentClassLoaders) {
            Container newContainer = classLoader.load(container);
            container.merge(newContainer);
        }

        return container;
    }
}
