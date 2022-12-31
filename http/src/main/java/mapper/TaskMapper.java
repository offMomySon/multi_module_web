package mapper;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import static validate.ValidateUtil.validateNull;

// TODO MethodIndicator 에 task 포함.
// TODO Task 생성방법을 따로 때어내는게 좋지 않을까?
// TODO Method 를 한번더 감쌀 수 있지 않을까?
// TODO 수직적인 요소들을 뽑아보자.
// TODO ClassAnnotationDetector 를 한번더 수직적으로 뽑아보자.
@Slf4j
@ToString
public class TaskMapper {
    private final Map<TaskIndicator, Method> values;

    public TaskMapper(Map<TaskIndicator, Method> values) {
        validateNull(values);

        this.values = createUnmodifiableUrlMethodMapper(values);
    }

    public Method findMethod(TaskIndicator taskIndicator) {
        return values.entrySet().stream()
            .filter(es -> es.getKey().equals(taskIndicator))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElseThrow(() -> new RuntimeException(
                MessageFormat.format("Does not exist, match url. given url : ", taskIndicator.getHttpUrl()))
            );
    }

    private static Map<TaskIndicator, Method> createUnmodifiableUrlMethodMapper(Map<TaskIndicator, Method> values) {
        return values.entrySet().stream()
            .filter(Objects::nonNull)
            .filter(es -> Objects.nonNull(es.getValue()))
            .map(es -> Map.entry(es.getKey(), es.getValue()))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (it, it1) -> it));
    }
}
