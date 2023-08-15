package com.main.task.value;

import com.main.util.AnnotationUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import matcher.annotation.RequestParam;
import matcher.converter.RequestParameters;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpUrlAnnotationAnnotatedParameterValueMatcher2Test {

    @DisplayName("parameter 로 부터 target annotation 을 찾지 못하면 exception 이 발생합니다.")
    @Test
    void test() {
        //given
        Parameter doesNotAnnotatedParameter = ParamAnnotatedClass.getDoesNotAnnotatedParameter(RequestParam.class);
        RequestParameters allParamHasRequestParameters = ParamAnnotatedClass.getAllParamHasRequestParameters();

        HttpUrlAnnotationAnnotatedParameterValueMatcher2 parameterValueMatcher = new HttpUrlAnnotationAnnotatedParameterValueMatcher2(RequestParam.class, allParamHasRequestParameters);
        //when
        Throwable actual = Assertions.catchThrowable(() -> parameterValueMatcher.match(doesNotAnnotatedParameter));

        //then
        Assertions.assertThat(actual).isNotNull();
    }

    @DisplayName("parameter 이름에 해당하는 값이 RequestParameters 에 존재하면 값을 찾아 옵니다.")
    @Test
    void ttest() {
        //given
        Class<RequestParam> annotationClazz = RequestParam.class;
        Parameter annotatedParameter = ParamAnnotatedClass.getAnnotatedParameter(annotationClazz, true);
        RequestParameters allParamHasRequestParameters = ParamAnnotatedClass.getAllParamHasRequestParameters();

        HttpUrlAnnotationAnnotatedParameterValueMatcher2 parameterValueMatcher = new HttpUrlAnnotationAnnotatedParameterValueMatcher2(annotationClazz, allParamHasRequestParameters);

        //when
        Optional<Object> optionalActual = parameterValueMatcher.match(annotatedParameter);

        //then
        Assertions.assertThat(optionalActual).isPresent();
    }

    @DisplayName("parameter 이름에 해당하는 값이, requestParameters 로 부터 값을 가져오지 못하면 빈값을 반환합니다.")
    @Test
    void ttttest() {
        //given
        Class<RequestParam> annotationClazz = RequestParam.class;
        Parameter doesNotMustMatchParameter = ParamAnnotatedClass.getAnnotatedParameter(annotationClazz, false);
        RequestParameters emptyRequestParameters = ParamAnnotatedClass.getEmptyRequestParameters();

        HttpUrlAnnotationAnnotatedParameterValueMatcher2 parameterValueMatcher = new HttpUrlAnnotationAnnotatedParameterValueMatcher2(annotationClazz, emptyRequestParameters);

        //when
        Optional optionalMatchValue = parameterValueMatcher.match(doesNotMustMatchParameter);

        //then
        Assertions.assertThat(optionalMatchValue).isEmpty();
    }


    public static class ParamAnnotatedClass {
        public static RequestParameters getAllParamHasRequestParameters() {
            return new RequestParameters(Map.of("param", "1",
                                                "param1", "1",
                                                "param2", "2",
                                                "param3", "3"
            ));
        }

        public static RequestParameters getEmptyRequestParameters() {
            return new RequestParameters(Collections.emptyMap());
        }

        public static Parameter getDoesNotAnnotatedParameter(Class<?> annotationClazz) {
            Objects.requireNonNull(annotationClazz);
            if (!annotationClazz.isAnnotation()) {
                throw new RuntimeException("does not annotation");
            }

            for (Parameter parameter : getTestMethodParameters()) {
                if (AnnotationUtils.exist(parameter, annotationClazz)) {
                    continue;
                }
                return parameter;
            }
            throw new RuntimeException("target annotation class does not annotated class.");
        }

        public static Parameter getAnnotatedParameter(Class<?> annotationClazz, boolean required) {
            Objects.requireNonNull(annotationClazz);
            if (!annotationClazz.isAnnotation()) {
                throw new RuntimeException("does not annotation");
            }

            for (Parameter parameter : getTestMethodParameters()) {
                Optional<?> optionalAnnotation = AnnotationUtils.find(parameter, annotationClazz);
                if (optionalAnnotation.isEmpty()) {
                    continue;
                }

                Annotation annotation = (Annotation) optionalAnnotation.get();
                HttpUrlAnnotationAnnotatedParameterValueMatcher.HttpUrlAnnotation httpUrlAnnotation = HttpUrlAnnotationAnnotatedParameterValueMatcher.HttpUrlAnnotation.from(annotation);

                if (httpUrlAnnotation.isRequired() != required) {
                    continue;
                }
                return parameter;
            }
            throw new RuntimeException(MessageFormat.format("does not exist annotattion : `{}`, and required `{}`.", annotationClazz, required));
        }

        private static Parameter[] getTestMethodParameters() {
            Method testMethod = getMethod();
            return testMethod.getParameters();
        }

        public int testMethod(int param,
                              @RequestParam(value = "param1") int param1,
                              @RequestParam(value = "param2", defaultValue = "2") int param2,
                              @RequestParam(value = "param3", required = false) int param3
        ) {
            return 1;
        }

        private static Method getMethod() {
            try {
                return ParamAnnotatedClass.class.getMethod("testMethod", int.class, int.class, int.class, int.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

}