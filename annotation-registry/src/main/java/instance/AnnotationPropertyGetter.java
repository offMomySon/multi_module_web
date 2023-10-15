package instance;

import annotation.AnnotationPropertyMappers;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import static com.main.util.AnnotationUtils.AnnotatedMethod;
import static com.main.util.AnnotationUtils.find;
import static com.main.util.AnnotationUtils.peekAnnotatedMethods;

// object, class, method, parameter 에 대해 찾고자하는 annotation 의 properties 를 가져온다.
// 어노테이션에 대해서 값을 가져오는 역할.

public class AnnotationPropertyGetter {
    private final AnnotationPropertyMappers propertyMappers;

    public AnnotationPropertyGetter(AnnotationPropertyMappers propertyMappers) {
        Objects.requireNonNull(propertyMappers);
        this.propertyMappers = propertyMappers;
    }

    public AnnotationProperties getAnnotationProperties(Object object, Class<?> findAnnotation, List<String> properties) {
        Class<?> objectClass = object.getClass();
        return getAnnotationProperties(objectClass, findAnnotation, properties);
    }

    public AnnotationProperties getAnnotationProperties(Class<?> clazz, Class<?> findAnnotation, List<String> properties) {
        if (Objects.isNull(clazz) || Objects.isNull(findAnnotation) || Objects.isNull(properties)) {
            throw new RuntimeException("Empty parameter.");
        }
        checkAnnotationClazz(findAnnotation);
        properties = excludeNull(properties);
        checkEmpty(properties);

        Optional<Annotation> optionalAnnotation = (Optional<Annotation>) find(clazz, findAnnotation);
        if (optionalAnnotation.isEmpty()) {
            return AnnotationProperties.empty();
        }

        Annotation annotation = optionalAnnotation.get();
        return propertyMappers.getPropertyValues(annotation, properties);
    }

    public AnnotationProperties getAnnotationProperties(Method javaMethod, Class<?> findAnnotation, List<String> properties) {
        if (Objects.isNull(javaMethod) || Objects.isNull(findAnnotation) || Objects.isNull(properties)) {
            throw new RuntimeException("Empty parameter.");
        }
        checkAnnotationClazz(findAnnotation);
        properties = excludeNull(properties);
        checkEmpty(properties);

        Optional<Annotation> optionalAnnotation = (Optional<Annotation>) find(javaMethod, findAnnotation);
        if (optionalAnnotation.isEmpty()) {
            return AnnotationProperties.empty();
        }

        Annotation annotation = optionalAnnotation.get();
        return propertyMappers.getPropertyValues(annotation, properties);
    }

    public AnnotationProperties getAnnotationProperties(Parameter parameter, Class<?> findAnnotation, List<String> properties) {
        if (Objects.isNull(parameter) || Objects.isNull(findAnnotation) || Objects.isNull(properties)) {
            throw new RuntimeException("Empty parameter.");
        }
        checkAnnotationClazz(findAnnotation);
        properties = excludeNull(properties);
        checkEmpty(properties);

        Optional<Annotation> optionalAnnotation = (Optional<Annotation>) find(parameter, findAnnotation);
        if (optionalAnnotation.isEmpty()) {
            return AnnotationProperties.empty();
        }

        Annotation annotation = optionalAnnotation.get();
        return propertyMappers.getPropertyValues(annotation, properties);
    }

    public List<AnnotatedMethodProperties> getAnnotationPropertiesMethodOfClazz(Class<?> clazz, Class<?> findAnnotation, List<String> _properties){
        if (Objects.isNull(clazz) || Objects.isNull(findAnnotation) || Objects.isNull(_properties)) {
            throw new RuntimeException("Empty parameter.");
        }
        checkAnnotationClazz(findAnnotation);
        List<String> properties = excludeNull(_properties);
        checkEmpty(properties);

        List<AnnotatedMethod> annotatedMethods = peekAnnotatedMethods(clazz, findAnnotation);
        return annotatedMethods.stream()
            .map(annotatedMethod -> {
                Method method = annotatedMethod.getMethod();
                Annotation annotation = annotatedMethod.getAnnotation();
                AnnotationProperties propertyValues = propertyMappers.getPropertyValues(annotation, properties);
                return new AnnotatedMethodProperties(method, propertyValues);
            })
            .collect(Collectors.toUnmodifiableList());
    }

    private static void checkAnnotationClazz(Class<?> findAnnotation) {
        if (!findAnnotation.isAnnotation()) {
            throw new RuntimeException("Does not annotation clazz.");
        }
    }

    private static List<String> excludeNull(List<String> properties) {
        return properties.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toUnmodifiableList());
    }

    private static void checkEmpty(List<String> properties) {
        if (properties.isEmpty()) {
            throw new RuntimeException("Empty parameter.");
        }
    }
}
