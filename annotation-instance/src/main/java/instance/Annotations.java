package instance;

import com.main.util.AnnotationUtils;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Annotations {
    // 중복 제거를 할 것 이냐?, 순회를 빠르게 할 것이냐?
    // 중복 제거에 중점을 두자!
    //   중복 제거를 해야 중복된 annotation 체크를 하지 않기 때문에
    //   순회의 성능이 빨라 질 것이다.
    // 다른 대안
    //   생성시에만 중복체크를 하고 instance 는 list 로 순회 속도를 높이자!
    private final List<Class<?>> values;

    public Annotations(List<Class<?>> values) {
        Objects.requireNonNull(values);

        this.values = values.stream()
            .filter(Class::isAnnotation)
            .distinct()
            .collect(Collectors.toUnmodifiableList());
    }

    public static Annotations empty() {
        return new Annotations(Collections.emptyList());
    }

    public Annotations merge(Annotations another) {
        if (Objects.isNull(another)) {
            return this;
        }

        List<Class<?>> mergedValues = Stream.concat(this.values.stream(), another.values.stream())
            .distinct()
            .collect(Collectors.toUnmodifiableList());
        return new Annotations(mergedValues);
    }

    public boolean anyAnnotatedFrom(Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            return false;
        }
        return values.stream().anyMatch(v -> AnnotationUtils.exist(clazz, v));
    }

    public boolean noneAnnotatedFrom(Class<?> clazz) {
        return !anyAnnotatedFrom(clazz);
    }

    public List<Class<?>> peekAnnotatedFieldsFrom(Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            return Collections.emptyList();
        }

        Field[] declaredFields = clazz.getDeclaredFields();
        return Arrays.stream(declaredFields)
            .map(Field::getType)
            .filter(fieldClazz -> AnnotationUtils.hasAny(fieldClazz, this.values))
            .collect(Collectors.toUnmodifiableList());
    }
}
