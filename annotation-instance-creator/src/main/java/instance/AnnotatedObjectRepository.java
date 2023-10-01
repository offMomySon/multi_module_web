package instance;

import annotation.AnnotationPropertyMapper.AnnotationProperties;
import annotation.AnnotationPropertyMappers;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;
import static instance.ReadOnlyObjectRepository.AnnotatedObject;

public class AnnotatedObjectRepository {
    private final ReadOnlyObjectRepository objectRepository;
    private final AnnotationPropertyMappers propertyMappers;

    public AnnotatedObjectRepository(ReadOnlyObjectRepository objectRepository, AnnotationPropertyMappers propertyMappers) {
        Objects.requireNonNull(objectRepository);
        Objects.requireNonNull(propertyMappers);
        this.objectRepository = objectRepository;
        this.propertyMappers = propertyMappers;
    }

    public <T> List<T> findObjectByClazz(Class<T> findClazz) {
        return objectRepository.findObjectByClazz(findClazz);
    }

    public List<AnnotatedObject> findObjectByAnnotatedClass(Class<?> findAnnotation) {
        return objectRepository.findObjectByAnnotatedClass(findAnnotation);
    }

    public List<AnnotatedObject> findObjectByClassAndAnnotatedClass(Class<?> findClazz, Class<?> findAnnotation) {
        return objectRepository.findObjectByClassAndAnnotatedClass(findClazz, findAnnotation);
    }

    // todo [review]
    // dto, method 이름이 모든걸 표현해주지 못하고있다.
    // 이름이 이미 길기 때문에 더 장황하게 설명하기 두렵다.
    // 아니면 class 의 개념이 잘못된 걸까..?

    // 1차 생각.
    // 해당 메서드는 값 저장소와 값 추출기라는 2개의 별개의 역할을 동시에 수행하고 있다.
    // 하나의 주제를 위해 역할을 수행하는 것이 아니라 하나의 시나리오를 위해 조합되고 있다.
    //   시나리오 - class, annotated class 에 해당하는 object 를 가져오고 해당 property 값을 추출한다.
    // 시나리오를 처리하기 위한 method 이기 때문에 뚜렷한 정책, 간단명료한 설명이 있는 것이 아니고 역할이 애매해 지고 있다.
    // 그렇기 때문에 아래 메서드를 분해해야한다.
    // 어노테이션의 특성을 추출하는 역할의 AnnotationPropertyExtractor 를 생성해서 아래의 조합을 분해한다.
    // 2차 생각.
    // AnnotationPropertyExtractor 를 생성할 필요가 없다.
    // 이미 property 로 annotation 의 값을 가져오는 역할의 AnnotationPropertyMapper 을 이용해서 조합으로 풀어내자.
    public List<AnnotatedObjectAndProperties> findObjectAndAnnotationPropertiesByClassAndAnnotatedClass(Class<?> findClazz, Class<?> findAnnotation, List<String> properties) {
        List<AnnotatedObject> annotatedObjects = findObjectByClassAndAnnotatedClass(findClazz, findAnnotation);

        return annotatedObjects.stream()
            .map(ao -> {
                Object object = ao.getObject();
                Annotation annotation = ao.getAnnotation();
                AnnotationProperties propertyValue = propertyMappers.getPropertyValues(annotation, properties);
                return new AnnotatedObjectAndProperties(object, propertyValue);
            })
            .collect(Collectors.toUnmodifiableList());
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
    }
}
