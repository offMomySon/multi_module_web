package instance;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.NonNull;
import static com.main.util.AnnotationUtils.exist;
import static com.main.util.AnnotationUtils.find;
import static instance.ObjectGraph.ReadOnlyObjectGraph;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toUnmodifiableMap;

public class AnnotatedClassObjectRepository {
    private final AnnotationPropertyGetter annotationPropertyGetter;
    private final Map<Class<?>, Object> values;

    public AnnotatedClassObjectRepository(@NonNull AnnotationPropertyGetter annotationPropertyGetter, @NonNull Map<Class<?>, Object> values) {
        this.annotationPropertyGetter = annotationPropertyGetter;
        this.values = values.entrySet().stream()
            .filter(entry -> nonNull(entry.getKey()))
            .filter(entry -> nonNull(entry.getValue()))
            .filter(entry -> hasDeclaredAnnotation(entry.getKey()))
            .collect(toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));
    }

    private static boolean hasDeclaredAnnotation(Class<?> clazz) {
        return clazz.getDeclaredAnnotations().length != 0;
    }

    public static AnnotatedClassObjectRepository from(@NonNull AnnotationPropertyGetter annotationPropertyGetter, @NonNull ReadOnlyObjectGraph objectGraph) {
        Map<Class<?>, Object> objectGraphValues = objectGraph.copyValues();
        return new AnnotatedClassObjectRepository(annotationPropertyGetter, objectGraphValues);
    }

    public Optional<Object> findObjectByMethod(@NonNull Method method) {
        return values.entrySet().stream()
            .filter(entry -> hasContainMethod(entry.getKey(), method))
            .map(Map.Entry::getValue)
            .findFirst();
    }

    private static boolean hasContainMethod(Class<?> clazz, Method method) {
        return Arrays.asList(clazz.getMethods()).contains(method);
    }

    public List<Class<?>> findClassByAnnotatedClass(@NonNull Class<?> findAnnotation) {
        List<Map.Entry<Class<?>, Object>> foundEntry = findEntryByAnnotatedClass(findAnnotation);
        return foundEntry.stream()
            .map(Map.Entry::getKey)
            .collect(Collectors.toUnmodifiableList());
    }

    public List<AnnotatedObjectAndMethodProperties> findAnnotatedObjectAndMethodPropertiesByClassAndAnnotatedClassFocusOnMethod(@NonNull List<Class<?>> findClasses,
                                                                                                                                @NonNull Class<?> findAnnotation,
                                                                                                                                @NonNull List<String> _properties) {
        return findClasses.stream()
            .map(findClazz -> findAnnotatedObjectAndMethodPropertiesByClassAndAnnotatedClassFocusOnMethod(findClazz, findAnnotation, _properties))
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableList());
    }

    public List<AnnotatedObject> findAnnotatedObjectByClassAndAnnotatedClass(@NonNull Class<?> findClazz, @NonNull Class<?> findAnnotation) {
        checkAnnotationClazz(findAnnotation);

        return values.entrySet().stream()
            .filter(entry -> findClazz.isAssignableFrom(entry.getKey()))
            .filter(entry -> exist(entry.getKey(), findAnnotation))
            .map(entry -> createAnnotatedObject(entry.getKey(), entry.getValue(), findAnnotation))
            .collect(Collectors.toUnmodifiableList());
    }

    public List<AnnotatedObjectProperties> findObjectAndAnnotationPropertiesByClassAndAnnotatedClass(@NonNull Class<?> findClazz, Class<?> findAnnotation,
                                                                                                     @NonNull List<String> _properties) {
        checkAnnotationClazz(findAnnotation);
        List<String> properties = excludeNull(_properties);
        checkEmpty(properties);

        List<AnnotatedObject> foundAnnotatedObject = findAnnotatedObjectByClassAndAnnotatedClass(findClazz, findAnnotation);
        return foundAnnotatedObject.stream()
            .map(annotatedObject -> {
                Object object = annotatedObject.getObject();
                AnnotationProperties annotationProperties = annotationPropertyGetter.getAnnotationProperties(object, findAnnotation, properties);
                return new AnnotatedObjectProperties(object, annotationProperties);
            })
            .collect(Collectors.toUnmodifiableList());
    }

    public AnnotatedClassObjectRepository append(@NonNull Class<?> clazz, @NonNull Object object) {
        Map<Class<?>, Object> newValues = new HashMap<>(values);
        newValues.put(clazz, object);
        return new AnnotatedClassObjectRepository(annotationPropertyGetter, newValues);
    }

    private List<AnnotatedObjectAndMethodProperties> findAnnotatedObjectAndMethodPropertiesByClassAndAnnotatedClassFocusOnMethod(Class<?> findClazz, Class<?> findAnnotation,
                                                                                                                                 List<String> _properties) {
        if (Objects.isNull(findClazz) || Objects.isNull(findAnnotation) || Objects.isNull(_properties)) {
            throw new RuntimeException("Empty parameter.");
        }
        checkAnnotationClazz(findAnnotation);
        List<String> properties = excludeNull(_properties);
        checkEmpty(properties);

        if (!values.containsKey(findClazz)) {
            return Collections.emptyList();
        }

        Object foundObject = values.get(findClazz);

        AnnotationProperties annotatedProperties = annotationPropertyGetter.getAnnotationProperties(findClazz, findAnnotation, properties);
        AnnotatedObjectProperties objectAndProperties = new AnnotatedObjectProperties(foundObject, annotatedProperties);

        List<AnnotatedMethodProperties> annotationPropertiesMethodOfClazz = annotationPropertyGetter.getAnnotationPropertiesMethodOfClazz(findClazz, findAnnotation, properties);
        return annotationPropertiesMethodOfClazz.stream()
            .map(annotatedMethodAndProperty -> new AnnotatedObjectAndMethodProperties(objectAndProperties, annotatedMethodAndProperty))
            .collect(Collectors.toUnmodifiableList());
    }

    private List<Map.Entry<Class<?>, Object>> findEntryByAnnotatedClass(Class<?> findAnnotation) {
        if (Objects.isNull(findAnnotation)) {
            throw new RuntimeException("Empty parameter.");
        }
        checkAnnotationClazz(findAnnotation);

        return values.entrySet().stream()
            .filter(entry -> exist(entry.getKey(), findAnnotation))
            .collect(Collectors.toUnmodifiableList());
    }

    private static AnnotatedObject createAnnotatedObject(Class<?> clazz, Object object, Class<?> findAnnotation) {
        Annotation annotation = (Annotation) find(clazz, findAnnotation).orElseThrow(() -> new RuntimeException("Does not exist annotation."));
        return new AnnotatedObject(object, annotation);
    }

    private static List<String> excludeNull(List<String> properties) {
        return properties.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toUnmodifiableList());
    }

    private static void checkAnnotationClazz(Class<?> findAnnotation) {
        if (!findAnnotation.isAnnotation()) {
            throw new RuntimeException("Does not annotation clazz.");
        }
    }

    private static void checkEmpty(List<String> properties) {
        if (properties.isEmpty()) {
            throw new RuntimeException("Empty parameter.");
        }
    }
}
