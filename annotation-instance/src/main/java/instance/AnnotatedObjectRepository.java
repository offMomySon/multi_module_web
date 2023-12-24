package instance;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import static com.main.util.AnnotationUtils.exist;
import static com.main.util.AnnotationUtils.find;
import static instance.ObjectGraph.ReadOnlyObjectGraph;

public class AnnotatedObjectRepository {
    private final Map<Class<?>, Object> values;

    public AnnotatedObjectRepository(@NonNull Map<Class<?>, Object> values) {
        this.values = Map.copyOf(values);
    }

    public static AnnotatedObjectRepository of(@NonNull ReadOnlyObjectGraph objectGraph) {
        Map<Class<?>, Object> objectGraphValues = objectGraph.copyValues();
        return new AnnotatedObjectRepository(objectGraphValues);
    }

    public Optional<Object> findObjectByMethod(@NonNull Method method) {
        return values.entrySet().stream()
            .filter(entry -> containMethod(entry.getKey(), method))
            .map(Map.Entry::getValue)
            .findFirst();
    }

    public List<Class<?>> findClassByAnnotatedClass(@NonNull Class<?> findAnnotation) {
        checkAnnotationClazz(findAnnotation);

        return values.keySet().stream()
            .filter(o -> exist(o, findAnnotation))
            .collect(Collectors.toUnmodifiableList());
    }

    public List<AnnotatedObject> findAnnotatedObjectByAnnotatedClass(@NonNull Class<?> findAnnotation) {
        checkAnnotationClazz(findAnnotation);

        return values.entrySet().stream()
            .filter(e -> exist(e.getKey(), findAnnotation))
            .map(e -> createAnnotatedObject(e.getKey(), e.getValue(), findAnnotation))
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

    private static boolean containMethod(Class<?> clazz, Method method) {
        return Arrays.asList(clazz.getMethods()).contains(method);
    }

    private static AnnotatedObject createAnnotatedObject(Class<?> clazz, Object object, Class<?> findAnnotation) {
        Annotation annotation = (Annotation) find(clazz, findAnnotation).orElseThrow(() -> new RuntimeException("Does not exist annotation."));
        return new AnnotatedObject(annotation, object);
    }

    private static void checkAnnotationClazz(Class<?> findAnnotation) {
        if (!findAnnotation.isAnnotation()) {
            throw new RuntimeException("Does not annotation clazz.");
        }
    }

    @Getter
    @EqualsAndHashCode
    public static class AnnotatedObject {
        private final Annotation annotation;
        private final Object object;

        public AnnotatedObject(@NonNull Annotation annotation, @NonNull Object object) {
            this.annotation = annotation;
            this.object = object;
        }
    }

    @Getter
    @EqualsAndHashCode
    public static class AnnotatedMethod {
        private final Annotation annotation;
        private final Method method;

        public AnnotatedMethod(@NonNull Annotation annotation, @NonNull Method method) {
            this.annotation = annotation;
            this.method = method;
        }
    }

    @Getter
    @EqualsAndHashCode
    public static class AnnotatedObjectMethod {
        private final AnnotatedObject annotatedObject;
        private final AnnotatedMethod annotatedMethod;

        public AnnotatedObjectMethod(@NonNull AnnotatedObject annotatedObject, @NonNull AnnotatedMethod annotatedMethod) {
            this.annotatedObject = annotatedObject;
            this.annotatedMethod = annotatedMethod;
        }
    }
}
