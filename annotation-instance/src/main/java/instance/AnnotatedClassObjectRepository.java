package instance;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import static com.main.util.AnnotationUtils.exist;
import static com.main.util.AnnotationUtils.find;
import static instance.ObjectGraph.ReadOnlyObjectGraph;

public class AnnotatedClassObjectRepository {
    private final AnnotationPropertyGetter annotationPropertyGetter;
    private final Map<Class<?>, Object> values;

    public AnnotatedClassObjectRepository(AnnotationPropertyGetter annotationPropertyGetter, Map<Class<?>, Object> values) {
        if (Objects.isNull(values)) {
            throw new RuntimeException("Invalid parameter. Empty param.");
        }
        this.annotationPropertyGetter = annotationPropertyGetter;
        this.values = values.entrySet().stream()
            .filter(entry -> !Objects.isNull(entry.getKey()))
            .filter(entry -> !Objects.isNull(entry.getValue()))
            .filter(entry -> hasDeclaredAnnotation(entry.getKey()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (prev, curr) -> prev));
    }

    private static boolean hasDeclaredAnnotation(Class<?> clazz) {
        return clazz.getDeclaredAnnotations().length != 0;
    }

    public static AnnotatedClassObjectRepository from(AnnotationPropertyGetter annotationPropertyGetter, ReadOnlyObjectGraph objectGraph) {
        if (Objects.isNull(objectGraph) || Objects.isNull(annotationPropertyGetter)) {
            throw new RuntimeException("Invalid param. Param is empty.");
        }

        Map<Class<?>, Object> objectGraphValues = objectGraph.copyValues();
        return new AnnotatedClassObjectRepository(annotationPropertyGetter, objectGraphValues);
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

    public List<Class<?>> findClassByAnnotatedClass(Class<?> findAnnotation) {
        List<Map.Entry<Class<?>, Object>> foundEntry = findEntryByAnnotatedClass(findAnnotation);
        return foundEntry.stream()
            .map(Map.Entry::getKey)
            .collect(Collectors.toUnmodifiableList());
    }

    public List<AnnotatedObjectAndMethodProperties> findAnnotatedObjectAndMethodPropertiesByClassAndAnnotatedClassFocusOnMethod(List<Class<?>> findClasses, Class<?> findAnnotation,
                                                                                                                                List<String> _properties) {
        return findClasses.stream()
            .map(findClazz -> findAnnotatedObjectAndMethodPropertiesByClassAndAnnotatedClassFocusOnMethod(findClazz, findAnnotation, _properties))
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableList());
    }

    //    // todo [review]
//    // dto, method 이름이 모든걸 표현해주지 못하고있다.
//    // 이름이 이미 길기 때문에 더 장황하게 설명하기 두렵다.
//    // 아니면 class 의 개념이 잘못된 걸까..?
//
//    // 1차 생각.
//    // 해당 메서드는 값 저장소와 값 추출기라는 2개의 별개의 역할을 동시에 수행하고 있다.
//    // 하나의 주제를 위해 역할을 수행하는 것이 아니라 하나의 시나리오를 위해 조합되고 있다.
//    //   시나리오 - class, annotated class 에 해당하는 object 를 가져오고 해당 property 값을 추출한다.
//    // 시나리오를 처리하기 위한 method 이기 때문에 뚜렷한 정책, 간단명료한 설명이 있는 것이 아니고 역할이 애매해 지고 있다.
//    // 그렇기 때문에 아래 메서드를 분해해야한다.
//    // 어노테이션의 특성을 추출하는 역할의 AnnotationPropertyExtractor 를 생성해서 아래의 조합을 분해한다.
//    // 2차 생각.
//    // AnnotationPropertyExtractor 를 생성할 필요가 없다.
//    // 이미 property 로 annotation 의 값을 가져오는 역할의 AnnotationPropertyMapper 을 이용해서 조합으로 풀어내자.
//    // 3차 생각.
//    // 조합으로 풀게 된다면 annotation 맥락을 main 에 노출되게 된다.
//    // 해당 맥락을 노출 시키지 않으려면 2개의 역할을 합친 개념이 나와야한다.
//    // ObjectRepository 의 역할을 살펴보자.
//    //  class 와 annotation 을 기반으로 object, annotation 을 찾는다.
//    // 위 역할을 보듯이, 애초에 objectRepository 네이밍이 잘못되었다.
//    // 왜냐하면, 기능에 연관된 핵심 키워드가 class, object, annotation 이기 때문이다.
//    // 역할에 어울리는 네이밍인 annotatedObjectRepository 라고 명명하자.
//    // 자연스럽게 annotation 의 property 값을 찾는 역할도 녹일 수 있다.
//    // 그래서. 이 클래스틑 폐기하고 기존의 objectRepository 의 네이밍을 변경하고 역할을 추가한다.
    private List<AnnotatedObjectAndMethodProperties> findAnnotatedObjectAndMethodPropertiesByClassAndAnnotatedClassFocusOnMethod(Class<?> findClazz, Class<?> findAnnotation, List<String> _properties) {
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

    public List<AnnotatedObject> findAnnotatedObjectByClassAndAnnotatedClass(Class<?> findClazz, Class<?> findAnnotation) {
        if (Objects.isNull(findClazz) || Objects.isNull(findAnnotation)) {
            throw new RuntimeException("Empty parameter.");
        }
        checkAnnotationClazz(findAnnotation);

        return values.entrySet().stream()
            .filter(entry -> findClazz.isAssignableFrom(entry.getKey()))
            .filter(entry -> exist(entry.getKey(), findAnnotation))
            .map(entry -> createAnnotatedObject(entry.getKey(), entry.getValue(), findAnnotation))
            .collect(Collectors.toUnmodifiableList());
    }

    public List<AnnotatedObjectProperties> findObjectAndAnnotationPropertiesByClassAndAnnotatedClass(Class<?> findClazz, Class<?> findAnnotation, List<String> _properties) {
        if (Objects.isNull(findClazz) || Objects.isNull(findAnnotation) || Objects.isNull(_properties)) {
            throw new RuntimeException("Empty parameter.");
        }
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

    public AnnotatedClassObjectRepository append(Class<?> clazz, Object object) {
        if(Objects.isNull(clazz) || Objects.isNull(object)){
            throw new RuntimeException("Must parameter not be null.");
        }

        Map<Class<?>, Object> newValues = new HashMap<>(values);
        newValues.put(clazz, object);
        return new AnnotatedClassObjectRepository(annotationPropertyGetter, newValues);
    }
}
