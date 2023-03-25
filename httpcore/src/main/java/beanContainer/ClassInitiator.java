package beanContainer;

import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.LinkedHashSet;
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

    public boolean containMemberClass(Class<?> clazz) {
        return instanceMemberClasses.contains(clazz);
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

    public Map<Class<?>, Object> loadInstance2(Map<Class<?>, Object> container) throws Exception {
        return doLoadIndstance(new LinkedHashSet<>(), container);
    }

    private Map<Class<?>, Object> doLoadIndstance(Set<Class<?>> visitClasses, Map<Class<?>, Object> container) throws Exception {
        boolean alreadyExistInstance = container.containsKey(clazz);
        if (alreadyExistInstance) {
            return container;
        }

        if (visitClasses.contains(clazz)) {
            String circularPath = visitClasses.stream()
                .map(Class::getSimpleName)
                .collect(Collectors.joining("->"));
            throw new RuntimeException("circular dependency detected. clazz : " + circularPath);
        }

        visitClasses.add(clazz);

        boolean standAlone = instanceMemberClasses.isEmpty();

        if (!standAlone) {
            for (Class<?> instanceMemberClasse : instanceMemberClasses) {
                ClassInitiator classInitiator = ClassInitiator.from(instanceMemberClasse, null);
                container = classInitiator.doLoadIndstance(visitClasses, container);
            }
        }

        List<Object> subClassInstances = instanceMemberClasses.stream().map(container::get).collect(Collectors.toUnmodifiableList());
        Constructor<?> declaredConstructor = clazz.getDeclaredConstructor(instanceMemberClasses.toArray(Class[]::new));
        Object instance = declaredConstructor.newInstance(subClassInstances);
        container.put(clazz, instance);

        visitClasses.remove(clazz);

        return container;
    }
}
