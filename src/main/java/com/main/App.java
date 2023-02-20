package com.main;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import mapper.AnnotationUtils;
import mapper.FileSystemUtil;
import mapper.MethodIndicator;
import mapper.marker.Controller;
import mapper.marker.RequestMapping;
import vo.HttpMethod;

@Slf4j
public class App {
    public static void main(String[] args) {
        // 파일 시스템에서 특정 패키지 하위의 모든 클래스를 찾는다.
        List<? extends Class<?>> classes = FileSystemUtil.find(App.class, "com.main");
        List<? extends Class<?>> filteredClasses = classes.stream()
            .filter(clazz -> AnnotationUtils.exist(clazz, Controller.class))
            .filter(clazz -> AnnotationUtils.exist(clazz, RequestMapping.class))
            .collect(Collectors.toUnmodifiableList());


        Map<? extends Class<?>, List<Method>> filteredClassAndMethods = filteredClasses.stream()
            .collect(Collectors.toUnmodifiableMap(Function.identity(),
                                                  clazz -> filterMethods(clazz.getMethods(), RequestMapping.class),
                                                  (x, y) -> x));

        List<RequestMappingMethod> requestMappingMethods = filteredClassAndMethods.entrySet().stream()
            .flatMap(entry -> {
                RequestMapping classRequestMapping = AnnotationUtils.find(entry.getKey(), RequestMapping.class).orElseThrow();
                return entry.getValue().stream()
                    .map(method -> {
                        RequestMapping methodRequestMapping = AnnotationUtils.find(method, RequestMapping.class).orElseThrow();
                        return new RequestMappingMethod(classRequestMapping, methodRequestMapping, method);
                    });
            })
            .collect(Collectors.toUnmodifiableList());


        List<MethodIndicator> methodIndicators1 = requestMappingMethods.stream()
            .flatMap(requestMappingMethod -> {
                List<HttpMethod> httpMethods = Arrays.stream(requestMappingMethod.getMethodRequestMapping().method()).collect(Collectors.toUnmodifiableList());
                List<String> classUrls = Arrays.stream(requestMappingMethod.getClassRequestMapping().value()).collect(Collectors.toUnmodifiableList());
                List<String> methodUrls = Arrays.stream(requestMappingMethod.getMethodRequestMapping().value()).collect(Collectors.toUnmodifiableList());

                return httpMethods.stream()
                    .flatMap(httpMethod -> classUrls.stream()
                        .flatMap(classUrl -> methodUrls.stream()
                            .map(methodUrl -> MethodIndicator.from(httpMethod, classUrl, methodUrl))
                        )
                    );
            })
            .collect(Collectors.toUnmodifiableList());


        List<MethodIndicator> methodIndicators = new ArrayList<>();
        for (Class<?> filteredClass : filteredClasses) {
            RequestMapping classRequestMapping = AnnotationUtils.find(filteredClass, RequestMapping.class).orElseThrow();

            List<Method> filteredMethods = Arrays.stream(filteredClass.getMethods())
                .filter(method -> AnnotationUtils.exist(method, RequestMapping.class))
                .collect(Collectors.toUnmodifiableList());

            for (Method filteredMethod : filteredMethods) {
                RequestMapping methodRequestMapping = AnnotationUtils.find(filteredMethod, RequestMapping.class).orElseThrow();

                HttpMethod[] httpMethods = methodRequestMapping.method();
                String[] classUrls = classRequestMapping.value();
                String[] methodUrls = methodRequestMapping.value();

                for (HttpMethod httpMethod : httpMethods) {
                    for (String classUrl : classUrls) {
                        for (String methodUrl : methodUrls) {

                            methodIndicators.add(MethodIndicator.from(httpMethod, classUrl, methodUrl));
                        }
                    }
                }

            }

        }
    }

    private static List<Method> filterMethods(Method[] methods, Class<?> annotationClazz) {
        return Arrays.stream(methods)
            .filter(method -> AnnotationUtils.exist(method, annotationClazz))
            .collect(Collectors.toUnmodifiableList());
    }

    @Getter
    private static class RequestMappingMethod {
        private final RequestMapping classRequestMapping;
        private final RequestMapping methodRequestMapping;
        private final Method method;

        public RequestMappingMethod(RequestMapping classRequestMapping, RequestMapping methodRequestMapping, Method method) {
            this.classRequestMapping = classRequestMapping;
            this.methodRequestMapping = methodRequestMapping;
            this.method = method;
        }
    }
}