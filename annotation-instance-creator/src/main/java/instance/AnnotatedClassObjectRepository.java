package instance;

import annotation.AnnotationPropertyMappers;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import static annotation.AnnotationPropertyMapper.AnnotationProperties;
import static com.main.util.AnnotationUtils.AnnotatedMethod;
import static com.main.util.AnnotationUtils.exist;
import static com.main.util.AnnotationUtils.find;
import static com.main.util.AnnotationUtils.peekAnnotatedMethods;
import static instance.ObjectGraph.ReadOnlyObjectGraph;

public class AnnotatedClassObjectRepository {
    private final AnnotationPropertyMappers propertyMappers;
    private final Map<Class<?>, Object> values;

    public AnnotatedClassObjectRepository(AnnotationPropertyMappers propertyMappers, Map<Class<?>, Object> values) {
        if (Objects.isNull(propertyMappers)) {
            propertyMappers = AnnotationPropertyMappers.empty();
        }
        if (Objects.isNull(values)) {
            values = Collections.emptyMap();
        }

        this.propertyMappers = propertyMappers;
        this.values = values.entrySet().stream()
            .filter(entry -> !Objects.isNull(entry.getKey()))
            .filter(entry -> !Objects.isNull(entry.getValue()))
            .filter(entry -> hasDeclaredAnnotation(entry.getKey()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));
    }

    private static boolean hasDeclaredAnnotation(Class<?> clazz) {
        return clazz.getDeclaredAnnotations().length != 0;
    }

    public static AnnotatedClassObjectRepository emtpy() {
        return new AnnotatedClassObjectRepository(AnnotationPropertyMappers.empty(), Collections.emptyMap());
    }

    public static AnnotatedClassObjectRepository from(AnnotationPropertyMappers propertyMappers, ReadOnlyObjectGraph objectGraph) {
        if (Objects.isNull(propertyMappers)) {
            propertyMappers = AnnotationPropertyMappers.empty();
        }
        if (Objects.isNull(objectGraph)) {
            objectGraph = ReadOnlyObjectGraph.empty();
        }

        Map<Class<?>, Object> rawObjectGraph = objectGraph.copyValues();
        return new AnnotatedClassObjectRepository(propertyMappers, rawObjectGraph);
    }

    public <T> List<T> findObjectByClazz(Class<T> findClazz) {
        if (Objects.isNull(findClazz)) {
            return Collections.emptyList();
        }

        return values.entrySet().stream()
            .filter(entry -> findClazz.isAssignableFrom(entry.getKey()))
            .map(entry -> (T) entry.getValue())
            .collect(Collectors.toUnmodifiableList());
    }

    private List<Map.Entry<Class<?>, Object>> findEntryByAnnotatedClass(Class<?> findAnnotation) {
        if (Objects.isNull(findAnnotation)) {
            throw new RuntimeException("Empty parameter.");
        }
        if (!findAnnotation.isAnnotation()) {
            throw new RuntimeException("Does not annotation clazz.");
        }

        return values.entrySet().stream()
            .filter(entry -> exist(entry.getKey(), findAnnotation))
            .collect(Collectors.toUnmodifiableList());
    }

    public List<Class<?>> findClassByAnnotatedClass(Class<?> findAnnotation) {
        List<Map.Entry<Class<?>, Object>> foundEntry = findEntryByAnnotatedClass(findAnnotation);
        return foundEntry.stream()
            .map(Map.Entry::getKey)
            .collect(Collectors.toUnmodifiableList());
    }

    public List<Object> findObjectByAnnotatedClass(Class<?> findAnnotation) {
        List<Map.Entry<Class<?>, Object>> foundEntry = findEntryByAnnotatedClass(findAnnotation);
        return foundEntry.stream()
            .map(Map.Entry::getValue)
            .collect(Collectors.toUnmodifiableList());
    }

    public List<AnnotatedObject> findAnnotatedObjectByAnnotatedClass(Class<?> findAnnotation) {
        List<Map.Entry<Class<?>, Object>> foundEntry = findEntryByAnnotatedClass(findAnnotation);
        return foundEntry.stream()
            .map(entry -> createAnnotatedObject(entry.getKey(), entry.getValue(), findAnnotation))
            .collect(Collectors.toUnmodifiableList());
    }


    public List<AnnotatedObjectAndMethodProperties> findAnnotatedObjectAndMethodPropertiesByClassAndAnnotatdClassAtMethodBase(List<Class<?>> findClasses, Class<?> findAnnotation,
                                                                                                                              List<String> _properties) {
        return findClasses.stream()
            .map(findClazz -> findAnnotatedObjectAndMethodPropertiesByClassAndAnnotatdClassAtMethodBase(findClazz, findAnnotation, _properties))
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableList());
    }

    // todo [review]
    // 이름이 너무 이상한데.
    public List<AnnotatedObjectAndMethodProperties> findAnnotatedObjectAndMethodPropertiesByClassAndAnnotatdClassAtMethodBase(Class<?> findClazz, Class<?> findAnnotation, List<String> _properties) {
        if (Objects.isNull(findClazz) || Objects.isNull(findAnnotation) || Objects.isNull(_properties)) {
            throw new RuntimeException("Empty parameter.");
        }
        if (!findAnnotation.isAnnotation()) {
            throw new RuntimeException("Does not annotation clazz.");
        }
        List<String> properties = _properties.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());
        if (properties.isEmpty()) {
            throw new RuntimeException("Empty parameter.");
        }

        if (!values.containsKey(findClazz)) {
            return Collections.emptyList();
        }

        Object foundObject = values.get(findClazz);
        Optional<Annotation> optionalAnnotation = (Optional<Annotation>) find(findClazz, findAnnotation);
        List<AnnotatedMethod> annotatedMethods = peekAnnotatedMethods(findClazz, findAnnotation);

        AnnotatedObjectAndProperties objectAndProperties = createAnnotatedObjectAndProperties(foundObject, optionalAnnotation, properties);
        List<AnnotatedMethodAndProperties> annotatedMethodAndProperties = annotatedMethods.stream()
            .map(annotatedMethod -> {
                Annotation annotation = annotatedMethod.getAnnotation();
                Method method = annotatedMethod.getMethod();
                AnnotationProperties propertyValues = propertyMappers.getPropertyValues(annotation, properties);
                return new AnnotatedMethodAndProperties(method, propertyValues);
            })
            .collect(Collectors.toUnmodifiableList());

        return annotatedMethodAndProperties.stream()
            .map(annotatedMethodAndProperty -> new AnnotatedObjectAndMethodProperties(objectAndProperties, annotatedMethodAndProperty))
            .collect(Collectors.toUnmodifiableList());
    }

    private AnnotatedObjectAndProperties createAnnotatedObjectAndProperties(Object foundObject, Optional<Annotation> optionalAnnotation, List<String> properties) {
        if (optionalAnnotation.isEmpty()) {
            return AnnotatedObjectAndProperties.emptyProperty(foundObject);
        }

        Annotation annotation = optionalAnnotation.get();
        AnnotationProperties propertyValues = propertyMappers.getPropertyValues(annotation, properties);
        return new AnnotatedObjectAndProperties(foundObject, propertyValues);
    }

    public List<AnnotatedObject> findAnnotatedObjectByClassAndAnnotatedClass(Class<?> findClazz, Class<?> findAnnotation) {
        if (Objects.isNull(findClazz) || Objects.isNull(findAnnotation)) {
            throw new RuntimeException("Empty parameter.");
        }
        if (!findAnnotation.isAnnotation()) {
            throw new RuntimeException("Does not annotation clazz.");
        }

        return values.entrySet().stream()
            .filter(entry -> findClazz.isAssignableFrom(entry.getKey()))
            .filter(entry -> exist(entry.getKey(), findAnnotation))
            .map(entry -> createAnnotatedObject(entry.getKey(), entry.getValue(), findAnnotation))
            .collect(Collectors.toUnmodifiableList());
    }

    public List<AnnotatedObjectAndProperties> findObjectAndAnnotationPropertiesByClassAndAnnotatedClass(Class<?> findClazz, Class<?> findAnnotation, List<String> _properties) {
        if (Objects.isNull(findClazz) || Objects.isNull(findAnnotation) || Objects.isNull(_properties)) {
            throw new RuntimeException("Empty parameter.");
        }
        if (!findAnnotation.isAnnotation()) {
            throw new RuntimeException("Does not annotation clazz.");
        }
        List<String> properties = _properties.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());
        if (properties.isEmpty()) {
            throw new RuntimeException("Empty parameter.");
        }

        List<AnnotatedObject> foundAnnotatedObject = findAnnotatedObjectByClassAndAnnotatedClass(findClazz, findAnnotation);
        return foundAnnotatedObject.stream()
            .map(annotatedObject -> {
                Object object = annotatedObject.getObject();
                Annotation annotation = annotatedObject.getAnnotation();

                AnnotationProperties annotationProperties = propertyMappers.getPropertyValues(annotation, _properties);
                return new AnnotatedObjectAndProperties(object, annotationProperties);
            })
            .collect(Collectors.toUnmodifiableList());
    }

    public AnnotatedParameterProperties extractProperties(Parameter parameter, Class<?> findAnnotation, List<String> _findProperties){
        if (Objects.isNull(parameter) || Objects.isNull(findAnnotation) || Objects.isNull(_findProperties)) {
            throw new RuntimeException("Empty parameter.");
        }
        if (!findAnnotation.isAnnotation()) {
            throw new RuntimeException("Does not annotation clazz.");
        }
        List<String> findProperties = _findProperties.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());
        if (findProperties.isEmpty()) {
            throw new RuntimeException("Empty parameter.");
        }

        Annotation annotation = (Annotation) find(parameter, findAnnotation).orElseThrow(() -> new RuntimeException("Does not exist find annotation."));
        AnnotationProperties propertyValues = propertyMappers.getPropertyValues(annotation, _findProperties);
        return new AnnotatedParameterProperties(parameter, propertyValues);
    }

//    public AnnotatedParameterProperties findParameterProperties(Class<?> findClazz, Method findMethod, Parameter findParameter, Class<?> findAnnotation, List<String> _findProperties) {
//        if (Objects.isNull(findClazz) || Objects.isNull(findMethod) || Objects.isNull(findParameter) || Objects.isNull(_findProperties)) {
//            throw new RuntimeException("Empty parameter.");
//        }
//        if (!findAnnotation.isAnnotation()) {
//            throw new RuntimeException("Does not annotation clazz.");
//        }
//        List<String> findProperties = _findProperties.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());
//        if (findProperties.isEmpty()) {
//            throw new RuntimeException("Empty parameter.");
//        }
//        if (!values.containsKey(findClazz)) {
//            throw new RuntimeException("Does not exist clazz.");
//        }
//
//        Method matchedMethod = Arrays.stream(findClazz.getMethods())
//            .filter(method -> method == findMethod)
//            .findFirst()
//            .orElseThrow(() -> new RuntimeException("Does not exist method."));
//
//        Arrays.stream(matchedMethod.getParameters())
//
//
//
//        return null;
//    }

    private static AnnotatedObject createAnnotatedObject(Class<?> clazz, Object object, Class<?> findAnnotation) {
        Annotation annotation = (Annotation) find(clazz, findAnnotation).orElseThrow(() -> new RuntimeException("Does not exist annotation."));
        return new AnnotatedObject(object, annotation);
    }

    @Getter
    public static class AnnotatedObjectAndMethodProperties {
        private final AnnotatedObjectAndProperties annotatedObjectAndProperties;
        private final AnnotatedMethodAndProperties annotatedMethodAndProperties;

        public AnnotatedObjectAndMethodProperties(AnnotatedObjectAndProperties annotatedObjectAndProperties, AnnotatedMethodAndProperties annotatedMethodAndProperties) {
            Objects.requireNonNull(annotatedObjectAndProperties);
            Objects.requireNonNull(annotatedMethodAndProperties);
            this.annotatedObjectAndProperties = annotatedObjectAndProperties;
            this.annotatedMethodAndProperties = annotatedMethodAndProperties;
        }
    }

    @Getter
    public static class AnnotatedObject {
        private final Object object;
        private final Annotation annotation;

        public AnnotatedObject(Object object, Annotation annotation) {
            Objects.requireNonNull(object);
            Objects.requireNonNull(annotation);
            this.object = object;
            this.annotation = annotation;
        }
    }

    @Getter
    public static class AnnotatedObjectAndProperties {
        private final Object object;
        private final AnnotationProperties annotationProperties;

        public AnnotatedObjectAndProperties(Object object, AnnotationProperties annotationProperties) {
            Objects.requireNonNull(object);
            Objects.requireNonNull(annotationProperties);
            this.object = object;
            this.annotationProperties = annotationProperties;
        }

        public static AnnotatedObjectAndProperties emptyProperty(Object object) {
            return new AnnotatedObjectAndProperties(object, AnnotationProperties.empty());
        }
    }

    @Getter
    public static class AnnotatedMethodAndProperties {
        private final Method javaMethod;
        private final AnnotationProperties annotationProperties;

        public AnnotatedMethodAndProperties(Method javaMethod, AnnotationProperties annotationProperties) {
            Objects.requireNonNull(javaMethod);
            Objects.requireNonNull(annotationProperties);
            this.javaMethod = javaMethod;
            this.annotationProperties = annotationProperties;
        }
    }

    @Getter
    public class AnnotatedParameterProperties {
        private final Parameter parameter;
        private final AnnotationProperties annotationProperties;

        public AnnotatedParameterProperties(Parameter parameter, AnnotationProperties annotationProperties) {
            Objects.requireNonNull(parameter);
            Objects.requireNonNull(annotationProperties);
            this.parameter = parameter;
            this.annotationProperties = annotationProperties;
        }
    }
}
