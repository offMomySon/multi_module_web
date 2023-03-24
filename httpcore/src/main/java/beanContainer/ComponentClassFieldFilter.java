package beanContainer;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import mapper.AnnotationUtils;
import mapper.marker.Component;

public class ComponentClassFieldFilter {
    private static final Class<?> COMPONENT_CLASS = Component.class;

    private final List<Class<?>> instanceMemberClasses;

    private ComponentClassFieldFilter(List<Class<?>> instanceMemberClasses) {
        this.instanceMemberClasses = instanceMemberClasses;
    }

    public static ComponentClassFieldFilter from(Class<?> clazz) {
        List<Class<?>> instanceMemberClasses = AnnotationUtils.peekFieldsType(clazz, COMPONENT_CLASS);

        return new ComponentClassFieldFilter(instanceMemberClasses);
    }

    public List<Class<?>> gatherContainFieldTypes(Set<Class<?>> parentClasses) {
        return instanceMemberClasses.stream()
            .filter(parentClasses::contains)
            .collect(Collectors.toUnmodifiableList());
    }
}
