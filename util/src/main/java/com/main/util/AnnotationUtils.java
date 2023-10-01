package com.main.util;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class AnnotationUtils {
    private static final Set<Class<?>> selfReferenceAnnotations = Set.of(Retention.class, Target.class, Documented.class);

    public static boolean doesNotExistAll(Class<?> clazz, Class<?>... _annotationClazzes) {
        return !existAll(clazz, _annotationClazzes);
    }

    public static List<Class<?>> filterByAnnotatedClazz(List<Class<?>> clazzes, Class<?> annotationClazz) {
        Objects.requireNonNull(clazzes);
        Objects.requireNonNull(annotationClazz);

        if (!annotationClazz.isAnnotation()) {
            return Collections.emptyList();
        }

        clazzes = clazzes.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());

        return clazzes.stream()
            .filter(clazz -> !Objects.isNull(clazz))
            .filter(clazz -> exist(clazz, annotationClazz))
            .collect(Collectors.toUnmodifiableList());
    }

    public static boolean existAll(Class<?> clazz, Class<?>... annotationClasses) {
        if (Objects.isNull(clazz) || Objects.isNull(annotationClasses) || annotationClasses.length == 0) {
            throw new RuntimeException("param is invalid.");
        }
        annotationClasses = (Class<?>[]) excludeEmptyElement(annotationClasses);
        if (annotationClasses.length == 0) {
            throw new RuntimeException("annoataionClazzes is empty.");
        }

        return Arrays.stream(annotationClasses)
            .allMatch(annotationClazz -> exist(clazz, annotationClazz));
    }

    public static boolean hasAny(Class<?> clazz, List<Class<?>> annotationClasses) {
        if (Objects.isNull(clazz) || Objects.isNull(annotationClasses) || annotationClasses.isEmpty()) {
            throw new RuntimeException("Invalid param.");
        }
        List<Class<?>> newAnnotationClasses = excludeEmptyElement(annotationClasses);
        if (newAnnotationClasses.isEmpty()) {
            throw new RuntimeException("annoataionClazzes is empty.");
        }

        return newAnnotationClasses.stream()
            .anyMatch(annotationClasse -> exist(clazz, annotationClasse));
    }

    public static List<Class<?>> peekFieldsType(Class<?> clazz, Class<?> annotationClass) {
        return Arrays.stream(clazz.getDeclaredFields())
            .map(Field::getType)
            .filter(typeClass -> AnnotationUtils.exist(typeClass, annotationClass))
            .collect(Collectors.toUnmodifiableList());
    }

    public static List<Method> peekMethods(Class<?> clazz, Class<?>... annotationClasses) {
        if (Objects.isNull(clazz) || Objects.isNull(annotationClasses) || annotationClasses.length == 0) {
            throw new RuntimeException("parma is invalid.");
        }
        Class<?>[] newAnnotationClasses = excludeEmptyElement(annotationClasses);
        if (newAnnotationClasses.length == 0) {
            throw new RuntimeException("annoataionClazzes is empty.");
        }

        Method[] declaredMethods = clazz.getDeclaredMethods();

        return Arrays.stream(declaredMethods)
            .filter(method -> Arrays.stream(newAnnotationClasses).allMatch(annotationClazz -> exist(method, annotationClazz)))
            .collect(Collectors.toUnmodifiableList());
    }


    public static boolean doesNotExist(Class<?> clazz, Class<?> annotationClazz) {
        return !exist(clazz, annotationClazz);
    }

    public static boolean doesNotExist(Parameter parameter, Class<?> annotationClazz) {
        return !exist(parameter, annotationClazz);
    }

    public static boolean exist(Field field, Class<?> annotationClazz) {
        return find(field, annotationClazz).isPresent();
    }

    public static boolean exist(Parameter param, Class<?> annotationClazz) {
        return find(param, annotationClazz).isPresent();
    }

    public static boolean exist(Class<?> clazz, Class<?> annotationClazz) {
        return find(clazz, annotationClazz).isPresent();
    }

    public static boolean exist(Method method, Class<?> annotationClazz) {
        return find(method, annotationClazz).isPresent();
    }

    public static <T> Optional<T> find(Field field, Class<T> annotationClazz) {
        return find(field.getDeclaredAnnotations(), annotationClazz);
    }

    public static <T> Optional<T> find(Class<?> clazz, Class<T> annotationClazz) {
        return find(clazz.getDeclaredAnnotations(), annotationClazz);
    }

    public static <T> Optional<T> find(Method method, Class<T> annotationClazz) {
        return find(method.getDeclaredAnnotations(), annotationClazz);
    }

    public static <T> Optional<T> find(Parameter parameter, Class<T> annotationClazz) {
        return find(parameter.getDeclaredAnnotations(), annotationClazz);
    }

    private static <T> Optional<T> find(Annotation[] annotations, Class<T> findAnnotationClazz) {
        if (Objects.isNull(annotations) || annotations.length == 0 || !findAnnotationClazz.isAnnotation()) {
            return Optional.empty();
        }

        return Arrays.stream(annotations)
            .map(annotation -> find(annotation, findAnnotationClazz))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findAny();
    }

    private static <T> Optional<T> find(Annotation annotation, Class<T> findAnnotationClazz) {
        if (Objects.isNull(annotation) || !findAnnotationClazz.isAnnotation()) {
            return Optional.empty();
        }

        if (selfReferenceAnnotations.contains(annotation.annotationType())) {
            return Optional.empty();
        }

        if (isAnnotationType(annotation, findAnnotationClazz)) {
            return Optional.of((T) annotation);
        }

        return Arrays.stream(annotation.annotationType().getAnnotations())
            .map(subAnnotation -> find(subAnnotation, findAnnotationClazz))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findAny();
    }

    private static boolean isAnnotationType(Annotation annotation, Class<?> annotationClass) {
        if (Objects.isNull(annotation) || Objects.isNull(annotationClass)) {
            return false;
        }
        return annotation.annotationType() == annotationClass;
    }

    private static Class<?>[] excludeEmptyElement(Class<?>[] elements) {
        return Arrays.stream(elements)
            .filter(Objects::nonNull)
            .toArray(Class<?>[]::new);
    }

    private static <T> List<T> excludeEmptyElement(List<T> elements) {
        return elements.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toUnmodifiableList());
    }
}
