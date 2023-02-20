package com.main;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import mapper.AnnotationUtils;
import mapper.FileSystemUtil;
import mapper.MethodIndicator;
import mapper.RequestMappingMethodDto;
import mapper.marker.Controller;
import mapper.marker.RequestMapping;
import vo.HttpMethod;

@Slf4j
public class App {
    // url 에 매칭되는 method 를 출력하는 MethodResolver 를 만드는 과정이다.
    public static void main(String[] args) {
        // 파일 시스템에서 특정 패키지 하위의 모든 클래스를 찾는다.
        // class 를 가져오는 이유는 class 의 method 를 객체화 하기 위해서 입니다.
        List<? extends Class<?>> classes = FileSystemUtil.find(App.class, "com.main");

        // 어노테이션 유틸은 클래스와 메서드에 존재하는 어노테이션을 존재검사, 찾기를 수행할 수 있다.
        // 찾아온 클래스에서 어노테이션 유틸로 특정 어노테이션들이 붙은 클래스를 필터링한다.
        // 해당 어노테이션은  controller, requestMapping 어노테이션이다.
        // requestMapping 어노테이션을 필터링하는 하는 이유는 필요한 url 정보를 가지고 있기 때문이다.
        List<Class<?>> controllerClasses = classes.stream()
            .filter(clazz -> AnnotationUtils.find(clazz, Controller.class).isPresent())
            .filter(clazz -> AnnotationUtils.find(clazz, RequestMapping.class).isPresent())
            .collect(Collectors.toUnmodifiableList());

        // class 와 class 하위의 method 를 필터링하여 grouping 한다. 필터링의 기준의 request 어노테이션이다
        // 그룹핑한 이유는 class 와 method 로 변환 연산을 같이 진행해야하기 때문이다.
        Map<Class<?>, List<Method>> classMethods = controllerClasses.stream()
            .collect(Collectors.toMap(Function.identity(), clazz -> filteredMethods(clazz, RequestMapping.class), (x, y) -> x));

        // 그룹핑한 class, method 들로 부터 특정 어노테이션을 추출하여 requestMappingMethod dto 들을 생성한다.
        // 특정 어노테이션은 requestMapping 어노테이션이다.
        // dto 를 생성하는 이유는 복잡한 class(N)와 method(M) 의 계층구조를 requestMappingMethod(N*M) 으로 평면화 하기 위해서 이다.
        List<RequestMappingMethodDto> requestMappingMethods = classMethods.entrySet().stream()
            .flatMap(e -> {
                RequestMapping classRequestMapping = AnnotationUtils.find(e.getKey(), RequestMapping.class).orElseThrow();
                return e.getValue().stream().map(method -> {
                    RequestMapping methodRequestMapping = AnnotationUtils.find(method, RequestMapping.class).orElseThrow();
                    return new RequestMappingMethodDto(classRequestMapping, methodRequestMapping, method);
                });
            })
            .collect(Collectors.toUnmodifiableList());

        // class 의 urls, method 의 urls, 그리고 methods 의 카타시안 곱으로 연산하여 methodIndicator 들을 생성한다.
        // methodIndicator 는 url 매칭 기능을 제공한다.
        List<MethodIndicator> methodIndicators = requestMappingMethods.stream()
            .flatMap(requestMappingMethod -> {
                HttpMethod[] httpMethods = requestMappingMethod.getMethodRequestMapping().method();
                String[] clazzUrls = requestMappingMethod.getClazzRequestMapping().value();
                String[] methodUrls = requestMappingMethod.getMethodRequestMapping().value();

                return Arrays.stream(httpMethods)
                    .flatMap(httpMethod -> Arrays.stream(clazzUrls)
                        .flatMap(clazzUrl -> Arrays.stream(methodUrls)
                            .map(methodUrl -> MethodIndicator.from(httpMethod, clazzUrl, methodUrl)))
                    );
            }).collect(Collectors.toUnmodifiableList());
    }

    private static List<Method> filteredMethods(Class<?> clazz, Class<?> annotationClazz) {
        return Arrays.stream(clazz.getMethods())
            .filter(method -> AnnotationUtils.exist(method, annotationClazz))
            .collect(Collectors.toUnmodifiableList());
    }
}