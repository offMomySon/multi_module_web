package matcher.converter;

import com.main.util.AnnotationUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import matcher.annotation.PathVariable;
import matcher.annotation.RequestParam;
import matcher.converter.base.ObjectConverter;


// 코드 분석.
// * 생성자.
//   1. 대상 annotation class 를 받는다.
//   2. 값 parameter 를 받는다.
@Slf4j
public class RequestParameterConverter implements ParameterConverter {
    private static final ObjectConverter objectConverter = new ObjectConverter();

    private final Class<?> targetAnnotationClazz;
    private final RequestParameters requestParameters;

    public RequestParameterConverter(Class<?> targetAnnotationClazz, RequestParameters requestParameters) {
        Objects.requireNonNull(targetAnnotationClazz);
        Objects.requireNonNull(requestParameters);

        this.targetAnnotationClazz = targetAnnotationClazz;
        this.requestParameters = requestParameters;
    }

//    1. parameter 를 받는다.
//    2. parameter 의 annotation 중에서 관심이 있는 annotation 이 존재하는지 확인한다.
//    3. annotation 으로부터 annotation value 를 가져온다.
//    4. parameter name 을 가져온다.
//    5. parameter name 이 존재하지 않으면 annotation value 의 name 을 가져온다.

//    6. annotation value 로 부터 deafult value 를 가져온다.
//    7. requestParam 으로 부터 parameter 이름에 일치하는 값을 가져온다. 존재하지 않으면 기본 값을 가져온다. ( - foundValue )

//    8. 어노테이션 값이 반드시 필요하고 foundValue 가 존재하지 않으면 exception 이 발생한다.
//    9. foundValue 가 존재하지 않으면 empty 값을 반환한다.

    //    10. foundValue 를 parameter type 에 따라 변환이 필요하지 않으면 그대로 반환한다.
//    11. foundValue 를 parameter type 에 따라 변환한다.
    @Override
    public Optional<Object> convertAsValue(Parameter parameter) {
        Objects.requireNonNull(parameter);

        Optional<?> optionalTargetAnnotation = AnnotationUtils.find(parameter, targetAnnotationClazz);

        boolean doesNotTargetAnnotation = optionalTargetAnnotation.isEmpty();
        if (doesNotTargetAnnotation) {
            return Optional.empty();
        }

        AnnotationValue annotationValue = AnnotationValue.from((Annotation) optionalTargetAnnotation.get());

        String bindName = parameter.getName();
        boolean existAnnotationName = !annotationValue.getName().isBlank();
        if (existAnnotationName) {
            bindName = annotationValue.getName();
        }

        String defaultValue = annotationValue.getDefaultValue().orElse(null);
        String foundValueOrNull = requestParameters.getOrDefault(bindName, defaultValue);

        boolean doesNotPossibleConvert = Objects.isNull(foundValueOrNull) && annotationValue.isRequired();
        if (doesNotPossibleConvert) {
            throw new RuntimeException("does not possible convert.");
        }

        if (Objects.isNull(foundValueOrNull)) {
            return Optional.empty();
        }

        boolean doesNotNecessaryTypeConvert = Objects.equals(foundValueOrNull.getClass(), parameter.getType());
        if (doesNotNecessaryTypeConvert) {
            return Optional.of(foundValueOrNull);
        }

        Object convertedValue = objectConverter.convert(foundValueOrNull, parameter.getType());
        return Optional.of(convertedValue);
    }


    private static class AnnotationValue {
        private final String name;
        private final Optional<String> defaultValue;
        private final boolean required;

        public AnnotationValue(String name, String defaultValue, boolean required) {
            Objects.requireNonNull(name);

            this.name = name;
            this.defaultValue = Optional.ofNullable(defaultValue);
            this.required = required;
        }

        public static AnnotationValue from(Annotation annotation) {
            if (annotation instanceof RequestParam) {
                RequestParam requestParam = (RequestParam) annotation;

                String defaultValue = requestParam.defaultValue().isBlank() ? null : requestParam.defaultValue();

                return new AnnotationValue(requestParam.value(), defaultValue, requestParam.required());
            }

            if (annotation instanceof PathVariable) {
                PathVariable pathVariable = (PathVariable) annotation;
                return new AnnotationValue(pathVariable.value(), null, pathVariable.required());
            }

            throw new RuntimeException("does not possible to create.");
        }


        public String getName() {
            return name;
        }

        public Optional<String> getDefaultValue() {
            return defaultValue;
        }

        public boolean isRequired() {
            return required;
        }
    }
}
