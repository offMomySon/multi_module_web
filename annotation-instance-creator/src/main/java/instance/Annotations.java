package instance;

import com.main.util.AnnotationUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    // todo [review]
    // todo [refac]
    // 중복제거를 위해 set 자료형을 제공하였다.
    // 장점
    //  사용자의 입장에서 중복이 없음을 알 수 있다.
    // 단점
    //  list 보다 순회의 속도가 느리기 때문에 순회로 사용하는 사용처가 많을 경우 속도가 느릴수 있다.
    // 이러한 단점에도 불구하고 set 을 선택한 이유는
    // 단점 - 순회가 필요한 경우 set -> list 로 변환 하여 사용하면 된다.
    // 장점이 주는 유일한 class 가 더 중요하다 판단되기 때문이다.
    // 2차 생각.
    // stream 의 흐름이 annotations 에 있다.
    // 만약 field 에 n 개의 어노테이션중 하나라도 있는것을 확인하고 싶다면.
    // field 에 annotation 이 존재하는지를 검사해야한다.
    // 따리서 아래의 코드는 변경되어야 한다.
    public Set<Class<?>> peekAnnotatedFieldsFrom(Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            return Collections.emptySet();
        }

        return values.stream()
            .map(targetAnnotation -> AnnotationUtils.peekFieldsType(clazz, targetAnnotation))
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableSet());
    }
}
