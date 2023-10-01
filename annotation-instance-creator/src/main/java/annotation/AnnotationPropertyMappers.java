package annotation;

import instance.ReadOnlyObjectRepository;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static annotation.AnnotationPropertyMapper.AnnotationProperties;

public class AnnotationPropertyMappers {
    private final Map<Class<?>, AnnotationPropertyMapper> mappers;

    public AnnotationPropertyMappers(Map<Class<?>, AnnotationPropertyMapper> mappers) {
        Objects.requireNonNull(mappers);
        this.mappers = mappers.entrySet().stream()
            .filter(entry -> Objects.nonNull(entry.getKey()))
            .filter(entry -> Objects.nonNull(entry.getValue()))
            .filter(entry -> isSupportClazz(entry.getKey(), entry.getValue()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));
    }

    private static boolean isSupportClazz(Class<?> clazz, AnnotationPropertyMapper propertyMapper) {
        return propertyMapper.isSupportAnnotation(clazz);
    }

    public AnnotationPropertyMappers merge(AnnotationPropertyMappers otherPropertyMappers) {
        Objects.requireNonNull(otherPropertyMappers);

        Stream<Map.Entry<Class<?>, AnnotationPropertyMapper>> baseMapperStream = this.mappers.entrySet().stream();
        Stream<Map.Entry<Class<?>, AnnotationPropertyMapper>> appendMapperStream = otherPropertyMappers.mappers.entrySet().stream();

        Map<Class<?>, AnnotationPropertyMapper> mergedMapper = Stream.concat(baseMapperStream, appendMapperStream)
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));
        return new AnnotationPropertyMappers(mergedMapper);
    }

    public AnnotationPropertyMappers append(Class<?> clazz, AnnotationPropertyMapper propertyMapper) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(propertyMapper);

        if (!clazz.isAnnotation()) {
            throw new RuntimeException("Does not annotation clazz.");
        }
        if (propertyMapper.doesNotSupportAnnotation(clazz)) {
            throw new RuntimeException("Does not support annotation.");
        }

        HashMap<Class<?>, AnnotationPropertyMapper> newTargetMapper = new HashMap<>(this.mappers);
        newTargetMapper.put(clazz, propertyMapper);
        return new AnnotationPropertyMappers(newTargetMapper);
    }

    public static AnnotationPropertyMappers empty() {
        return new AnnotationPropertyMappers(Collections.emptyMap());
    }

    public AnnotationProperties getPropertyValues(Annotation annotation, List<String> properties) {
        if (Objects.isNull(annotation) || Objects.isNull(properties)) {
            return AnnotationProperties.empty();
        }
        Class<? extends Annotation> annotationType = annotation.annotationType();

        if (!mappers.containsKey(annotationType)) {
            return AnnotationProperties.empty();
        }

        AnnotationPropertyMapper annotationPropertyMapper = mappers.get(annotationType);
        return annotationPropertyMapper.getPropertyValue(annotation, properties);
    }
}
