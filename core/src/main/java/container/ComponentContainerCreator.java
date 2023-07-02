package container;

import container.annotation.Component;
import lombok.extern.slf4j.Slf4j;
import util.AnnotationUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public ObjectRepository create() {
        List<ComponentClassInitializer> componentClassInitializers = componentClasses.stream()
                .map(ComponentClassInitializer::new)
                .collect(Collectors.toUnmodifiableList());

        ObjectRepository objectRepository = ObjectRepository.empty();
        for (ComponentClassInitializer classLoader : componentClassInitializers) {
            ObjectRepository newObjectRepository = classLoader.load(objectRepository);
            objectRepository.merge(newObjectRepository);
        }

        return objectRepository;
    }
}
