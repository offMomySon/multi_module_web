package instance;

import java.lang.annotation.Annotation;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
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

    public List<Class<?>> findClassByClassAnnotatedClass(@NonNull Class<?> findAnnotation) {
        checkAnnotationClazz(findAnnotation);

        return findAnnotatedObjectByClassAnnotatedClazz(findAnnotation)
            .stream()
            .map(ao -> ao.getObject().getClass())
            .collect(Collectors.toUnmodifiableList());
    }

    public List<AnnotatedObject> findAnnotatedObjectByClassAndClassAnnotatedClass(@NonNull Class<?> findClazz, @NonNull Class<?> findAnnotation) {
        checkAnnotationClazz(findAnnotation);

        return getAssignableObjects(findClazz)
            .stream()
            .map(object -> new ObjectAnnotatedChecker(object, findAnnotation))
            .map(ObjectAnnotatedChecker::isAnnotated)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toUnmodifiableList());
    }

    public List<AnnotatedObjectMethod> findAnnotatedObjectMethodByClassAndMethodAnnotatedClass(@NonNull Class<?> findAnnotationClazz) {
        checkAnnotationClazz(findAnnotationClazz);

        return findAnnotatedObjectByClassAnnotatedClazz(findAnnotationClazz)
            .stream()
            .flatMap(ao -> ao.createAnnotatedObjectMethod(findAnnotationClazz).stream())
            .collect(Collectors.toUnmodifiableList());
    }

    public List<AnnotatedObjectMethod> findAnnotatedObjectMethodByClassAnnotatedClassAndMethodAnnotatedClass(@NonNull Class<?> findClassAnnotatedClass,
                                                                                                             @NonNull Class<?> findClassMethodAnnotatedClass) {
        checkAnnotationClazz(findClassAnnotatedClass);
        checkAnnotationClazz(findClassMethodAnnotatedClass);

        return findAnnotatedObjectByClassAnnotatedClazz(findClassAnnotatedClass).stream()
            .flatMap(ao -> ao.createAnnotatedObjectMethod(findClassMethodAnnotatedClass).stream())
            .collect(Collectors.toUnmodifiableList());
    }

    public List<AnnotatedObject> findAnnotatedObjectByClassAnnotatedClazz(Class<?> findAnnotationClazz) {
        checkAnnotationClazz(findAnnotationClazz);

        return values.values().stream().map(object -> new ObjectAnnotatedChecker(object, findAnnotationClazz))
            .map(ObjectAnnotatedChecker::isAnnotated)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toUnmodifiableList());
    }

    private List<Object> getAssignableObjects(Class<?> findClazz) {
        return values.entrySet().stream()
            .filter(e -> findClazz.isAssignableFrom(e.getKey()))
            .map(Map.Entry::getValue)
            .collect(Collectors.toUnmodifiableList());
    }

    private static void checkAnnotationClazz(Class<?> findAnnotation) {
        if (!findAnnotation.isAnnotation()) {
            throw new RuntimeException("Does not annotation clazz.");
        }
    }

    private static boolean containMethod(Class<?> clazz, Method method) {
        return Arrays.asList(clazz.getMethods()).contains(method);
    }

    private static class ObjectAnnotatedChecker {
        private final Object object;
        private final Class<?> annotationClazz;

        public ObjectAnnotatedChecker(@NonNull Object object, @NonNull Class<?> annotationClazz) {
            this.object = object;
            this.annotationClazz = annotationClazz;
        }

        public Optional<AnnotatedObject> isAnnotated() {
            Class<?> objectClass = object.getClass();
            Optional<Annotation> optionalAnnotation = (Optional<Annotation>) find(objectClass, annotationClazz);

            if (optionalAnnotation.isEmpty()) {
                return Optional.empty();
            }

            Annotation annotation = optionalAnnotation.get();
            AnnotatedObject annotatedObject = new AnnotatedObject(annotation, this.object);
            return Optional.of(annotatedObject);
        }
    }

    private static class MethodAnnotatedChecker {
        private final Method method;
        private final Class<?> annotationClazz;

        public MethodAnnotatedChecker(@NonNull Method method, @NonNull Class<?> annotationClazz) {
            this.method = method;
            this.annotationClazz = annotationClazz;
        }

        public Optional<AnnotatedMethod> check() {
            Optional<Annotation> optionalAnnotation = (Optional<Annotation>) find(method, annotationClazz);

            if (optionalAnnotation.isEmpty()) {
                return Optional.empty();
            }

            Annotation annotation = optionalAnnotation.get();
            AnnotatedMethod annotatedMethod = new AnnotatedMethod(annotation, this.method);
            return Optional.of(annotatedMethod);
        }
    }

    @ToString
    @Getter
    @EqualsAndHashCode
    public static class AnnotatedObject {
        private final Annotation annotation;
        private final Object object;

        public AnnotatedObject(@NonNull Annotation annotation, @NonNull Object object) {
            this.annotation = annotation;
            this.object = object;
        }

        public ObjectAnnotatedChecker nextChecker(@NonNull Class<?> annotationClazz) {
            checkAnnotationClazz(annotationClazz);
            return new ObjectAnnotatedChecker(this.object, annotationClazz);
        }

        public List<AnnotatedObjectMethod> createAnnotatedObjectMethod(@NonNull Class<?> methodAnnotatedClazz) {
            checkAnnotationClazz(methodAnnotatedClazz);

            List<AnnotatedMethod> annotatedMethods = Arrays.stream(object.getClass().getDeclaredMethods())
                .map(dm -> new MethodAnnotatedChecker(dm, methodAnnotatedClazz))
                .map(MethodAnnotatedChecker::check)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toUnmodifiableList());

            return annotatedMethods.stream()
                .map(am -> new AnnotatedObjectMethod(this, am))
                .collect(Collectors.toUnmodifiableList());
        }
    }

    @ToString
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

    @ToString
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
