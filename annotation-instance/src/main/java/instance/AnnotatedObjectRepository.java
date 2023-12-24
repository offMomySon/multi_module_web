package instance;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

    public List<Class<?>> findClassByAnnotationClass(@NonNull Class<?> findAnnotation) {
        checkAnnotationClazz(findAnnotation);

        return findClassAnnotatedEntryByAnnotationClass(findAnnotation)
            .map(Map.Entry::getKey)
            .collect(Collectors.toUnmodifiableList());
    }

    public List<AnnotatedObject> findAnnotatedObjectByClassAndAnnotationClass(@NonNull Class<?> findClazz, @NonNull Class<?> findAnnotation) {
        checkAnnotationClazz(findAnnotation);

        return values.entrySet().stream()
            .filter(entry -> findClazz.isAssignableFrom(entry.getKey()))
            .filter(entry -> isAnnotated(entry.getKey(), findAnnotation))
            .map(entry -> createAnnotatedObject(entry.getKey(), entry.getValue(), findAnnotation))
            .collect(Collectors.toUnmodifiableList());
    }

    public List<AnnotatedObjectMethod> findAnnotatedObjectMethodByAnnotationClass(@NonNull Class<?> findAnnotation) {
        checkAnnotationClazz(findAnnotation);

        return findAnnotatedObjectByAnnotationClass(findAnnotation)
            .stream()
            .flatMap(AnnotatedObjectRepository::createInnerAnnotatedObjectMethod)
            .map(iaom -> iaom.createAnnotatedObjectMethod(findAnnotation))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toUnmodifiableList());
    }

    private static Stream<InnerAnnotatedObjectMethod> createInnerAnnotatedObjectMethod(AnnotatedObject ao) {
        return Arrays.stream(ao.getObject().getClass().getDeclaredMethods())
            .map(method -> new InnerAnnotatedObjectMethod(ao.getAnnotation(), ao.getObject(), method));
    }

    public List<AnnotatedObject> findAnnotatedObjectByAnnotationClass(@NonNull Class<?> findAnnotation) {
        checkAnnotationClazz(findAnnotation);

        return findClassAnnotatedEntryByAnnotationClass(findAnnotation)
            .map(e -> createAnnotatedObject(e.getKey(), e.getValue(), findAnnotation))
            .collect(Collectors.toUnmodifiableList());
    }

    private static class InnerAnnotatedObjectMethod {
        private final Annotation annotation;
        private final Object object;
        private final Method method;

        public InnerAnnotatedObjectMethod(@NonNull Annotation annotation, @NonNull Object object, @NonNull Method method) {
            this.annotation = annotation;
            this.object = object;
            this.method = method;
        }

        public Optional<AnnotatedObjectMethod> createAnnotatedObjectMethod(@NonNull Class<?> findAnnotation) {
            Optional<Annotation> optionalAnnotation = (Optional<Annotation>) find(method, findAnnotation);

            if (optionalAnnotation.isEmpty()) {
                return Optional.empty();
            }

            Annotation methodAnnotation = optionalAnnotation.get();

            AnnotatedObject annotatedObject = new AnnotatedObject(this.annotation, this.object);
            AnnotatedMethod annotatedMethod = new AnnotatedMethod(methodAnnotation, this.method);
            AnnotatedObjectMethod annotatedObjectMethod = new AnnotatedObjectMethod(annotatedObject, annotatedMethod);
            return Optional.of(annotatedObjectMethod);
        }
    }

    private static void checkAnnotationClazz(Class<?> findAnnotation) {
        if (!findAnnotation.isAnnotation()) {
            throw new RuntimeException("Does not annotation clazz.");
        }
    }

    private Stream<Map.Entry<Class<?>, Object>> findClassAnnotatedEntryByAnnotationClass(Class<?> findAnnotation) {
        return values.entrySet().stream()
            .filter(entry -> isAnnotated(entry.getKey(), findAnnotation));
    }

    private static boolean isAnnotated(Class<?> clazz, Class<?> findAnnotation) {
        return exist(clazz, findAnnotation);
    }

    private static boolean isAnnotated(Method method, Class<?> findAnnotation) {
        return exist(method, findAnnotation);
    }

    private static boolean containMethod(Class<?> clazz, Method method) {
        return Arrays.asList(clazz.getMethods()).contains(method);
    }

    private static AnnotatedObjectMethod createAnnotatedObjectMethod(Class<?> clazz, Object object, Method method, Class<?> findAnnotation) {
        AnnotatedObject annotatedObject = createAnnotatedObject(clazz, object, findAnnotation);
        AnnotatedMethod annotatedMethod = createAnnotatedMethod(method, findAnnotation);
        return new AnnotatedObjectMethod(annotatedObject, annotatedMethod);
    }

    private static AnnotatedObject createAnnotatedObject(Class<?> clazz, Object object, Class<?> findAnnotation) {
        Annotation annotation = (Annotation) find(clazz, findAnnotation).orElseThrow(() -> new RuntimeException("Does not exist annotation."));
        return new AnnotatedObject(annotation, object);
    }

    private static AnnotatedMethod createAnnotatedMethod(Method method, Class<?> findAnnotation) {
        Annotation annotation = (Annotation) find(method, findAnnotation).orElseThrow(() -> new RuntimeException("Does not exist annotation."));
        return new AnnotatedMethod(annotation, method);
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
