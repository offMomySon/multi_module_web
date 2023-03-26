package beanContainer;

import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import mapper.AnnotationUtils;
import mapper.marker.Component;

public class ClassInitiator {
    private Class<?> clazz;
    private List<Class<?>> instanceMemberClasses;
    private Set<Class<?>> parentClasses;

    public ClassInitiator(Class<?> clazz, List<Class<?>> instanceMemberClasses, Set<Class<?>> parentClasses) {
        this.clazz = clazz;
        this.instanceMemberClasses = instanceMemberClasses;
        this.parentClasses = parentClasses;
    }

    public static ClassInitiator from(Class<?> clazz) {
        List<Class<?>> instanceMemberClasses = AnnotationUtils.peekFieldsType(clazz, Component.class);
        return new ClassInitiator(clazz, instanceMemberClasses, Collections.emptySet());
    }

    public static ClassInitiator from(Class<?> clazz, Set<Class<?>> parentClasses) {
        List<Class<?>> instanceMemberClasses = AnnotationUtils.peekFieldsType(clazz, Component.class);

        return new ClassInitiator(clazz, instanceMemberClasses, parentClasses);
    }

    public Map<Class<?>, Object> loadInstance(Map<Class<?>, Object> container) {
        try {
            if (instanceMemberClasses.isEmpty()) {
                Constructor<?> constructor = clazz.getConstructor();
                Object instance = constructor.newInstance();

                container.put(clazz, instance);
                return container;
            }

            parentClasses.add(clazz);

            List<Class<?>> circularReferenceClasses = instanceMemberClasses.stream()
                .filter(parentClasses::contains)
                .collect(Collectors.toUnmodifiableList());

            if (!circularReferenceClasses.isEmpty()) {
                String recursiveClassNames = circularReferenceClasses.stream()
                    .map(Class::getSimpleName)
                    .collect(Collectors.joining(",", "[", "]"));

                throw new RuntimeException(MessageFormat.format("current class : `{0}`, recursiveClasses : `{1}`", clazz.getSimpleName(), recursiveClassNames));
            }

            for (Class<?> instanceMemberClazz : instanceMemberClasses) {
                ClassInitiator classInitiator = ClassInitiator.from(instanceMemberClazz, parentClasses);
                container = classInitiator.loadInstance(container);
            }

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
