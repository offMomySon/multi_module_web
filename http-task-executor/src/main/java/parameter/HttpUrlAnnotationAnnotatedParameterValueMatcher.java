package parameter;

import annotation.PathVariable;
import annotation.RequestParam;
import parameter.MethodParameterValueMatcher;
import parameter.RequestParameters;
import com.main.util.AnnotationUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

// 5. todo [annotation]
// parameter 에 RequestParam, PathVariable 어노테이션이 존재하는지 검증한다.
// 해당 어노테이팅이 필요한지 체크하고
// 필요하다면 annotation 모듈의 역할로 처리가 가능하게 수정하자.
public class HttpUrlAnnotationAnnotatedParameterValueMatcher<T> implements MethodParameterValueMatcher {
    private static final String EMPTY_VALUE = null;
    private static final Set<Class<?>> HTTP_URL_ANNOTATION_CLASSES = Set.of(RequestParam.class, PathVariable.class);

    private final Class<T> paramAnnotationClazz;
    private final RequestParameters requestParameters;

    public HttpUrlAnnotationAnnotatedParameterValueMatcher(Class<T> parameterAnnotationClazz, RequestParameters requestParameters) {
        Objects.requireNonNull(parameterAnnotationClazz);
        Objects.requireNonNull(requestParameters);

        boolean doesNotBaseParameterAnnotation = !HTTP_URL_ANNOTATION_CLASSES.contains(parameterAnnotationClazz);
        if (doesNotBaseParameterAnnotation) {
            throw new RuntimeException(MessageFormat.format("does not base annotation. parameterAnnotationClazz : `{}`", parameterAnnotationClazz));
        }

        this.paramAnnotationClazz = parameterAnnotationClazz;
        this.requestParameters = requestParameters;
    }

    @Override
    public Optional<?> match(Parameter parameter) {
        Objects.requireNonNull(parameter);

        Optional<T> optionalParameterAnnotation = AnnotationUtils.find(parameter, paramAnnotationClazz);
        if (optionalParameterAnnotation.isEmpty()) {
            throw new RuntimeException(MessageFormat.format("does not exist annotation. parameter : `{}`, paramAnnotationClazz : `{}`", parameter, paramAnnotationClazz));
        }

        Annotation annotation = (Annotation) optionalParameterAnnotation.get();
        HttpUrlAnnotation httpUrlAnnotation = HttpUrlAnnotation.from(annotation);
        String bindName = !httpUrlAnnotation.isParameterNameBlank() ?
            httpUrlAnnotation.getParameterName() :
            parameter.getName();

        String matchValue = requestParameters.getOrDefault(bindName, EMPTY_VALUE);
        boolean doesNotPossibleMatchValue = Objects.isNull(matchValue) && httpUrlAnnotation.isRequired();
        if (doesNotPossibleMatchValue) {
            throw new RuntimeException("Does not Possible match value, value must be exist.");
        }

        boolean doesNotExistMatchValue = Objects.isNull(matchValue);
        if (doesNotExistMatchValue) {
            return httpUrlAnnotation.getDefaultValue();
        }

        return Optional.of(matchValue);
    }


    // 5. todo [annotation]
    // parameter 에 RequestParam, PathVariable 어노테이션의 값변환을 수행하고 있다.
    // 해당 역할을 annotation 모듈에서 처리가 가능하도록 역할을 이전한다.
    public static class HttpUrlAnnotation {
        private static final String EMPTY_VALUE = null;

        private final String parameterName;
        private final Optional<String> defaultValue;
        private final boolean required;

        public HttpUrlAnnotation(String parameterName, String defaultValue, boolean required) {
            Objects.requireNonNull(parameterName);
            this.parameterName = parameterName;
            this.defaultValue = Optional.ofNullable(defaultValue);
            this.required = required;
        }

        public static HttpUrlAnnotation from(Annotation annotation) {
            Objects.requireNonNull(annotation);

            if (annotation instanceof RequestParam) {
                RequestParam requestParam = (RequestParam) annotation;
                String parameterName = requestParam.value();
                String defaultValue = requestParam.defaultValue().isBlank() ? EMPTY_VALUE : requestParam.defaultValue();
                boolean required = requestParam.required();

                return new HttpUrlAnnotation(parameterName, defaultValue, required);
            }

            if (annotation instanceof PathVariable) {
                PathVariable pathVariable = (PathVariable) annotation;
                String parameterName = pathVariable.value();
                boolean required = pathVariable.required();
                return new HttpUrlAnnotation(parameterName, EMPTY_VALUE, required);
            }

            throw new RuntimeException(MessageFormat.format("does not possible create ParameterName whit this Annotation. Annotation : `{}`", annotation));
        }

        public boolean isParameterNameBlank() {
            return parameterName.isBlank();
        }

        public String getParameterName() {
            return parameterName;
        }

        public Optional<String> getDefaultValue() {
            return defaultValue;
        }

        public boolean isRequired() {
            return required;
        }
    }
}
