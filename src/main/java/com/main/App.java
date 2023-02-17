package com.main;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import mapper.AnnotationUtils;
import mapper.FileSystemUtil;
import mapper.FilteredClassMethods;
import mapper.MethodIndicator;
import mapper.MethodResolver;
import mapper.marker.Controller;
import mapper.marker.RequestMapping;
import vo.HttpMethod;

@Slf4j
public class App {
    public static void main(String[] args) {
        // 파일 시스템에서 특정 패키지 하위의 모든 클래스를 찾는다.
        List<? extends Class<?>> classes = FileSystemUtil.find(App.class, "com.main");

        // 어노테이션 유틸은 클래스와 메서드에 존재하는 어노테이션을 존재검사, 찾기를 수행할 수 있다.
        // 찾아온 클래스에서 어노테이션 유틸로 특정 어노테이션들이 붙은 클래스를 필터링한다.
        // 해당 어노테이션은  controller, requestMapping 어노테이션이다.
        List<Class<?>> controllerClasses = classes.stream()
            .filter(clazz -> AnnotationUtils.find(clazz, Controller.class).isPresent())
            .filter(clazz -> AnnotationUtils.find(clazz, RequestMapping.class).isPresent())
            .collect(Collectors.toUnmodifiableList());

        // 클래스에 존재하는 메서드중에서 특정 어노테이션이 붙은 메서드를 필터링한다.
        // 해당 어노테이션은 requestMapping 어노테이션이다.
        List<FilteredClassMethods> filteredClassMethods = new ArrayList<>();
        for (Class<?> clazz : controllerClasses) {
            List<Method> requestMappingMethods = Arrays.stream(clazz.getMethods())
                .filter(method -> AnnotationUtils.find(method, RequestMapping.class).isPresent())
                .collect(Collectors.toUnmodifiableList());

            filteredClassMethods.add(new FilteredClassMethods(clazz, requestMappingMethods));
        }

        // 필터링한 class 와 method 에서 특정 어노테이션을 가져옵니다.
        // 해당 어노테이션은 class 은 request mapping 어노테이션이고, method 에서는 request mapping 어노테이션 입니다.
        List<MethodResolver> methodResolvers = new ArrayList<>();
        for (FilteredClassMethods filteredClassMethod : filteredClassMethods) {
            RequestMapping clazzRequestMapping = AnnotationUtils.find(filteredClassMethod.getClazz(), RequestMapping.class)
                .orElseThrow(() -> new RuntimeException("not exist"));
            for (Method method : filteredClassMethod.getMethods()) {
                RequestMapping methodRequestMapping = AnnotationUtils.find(method, RequestMapping.class).orElseThrow(() -> new RuntimeException("not exist"));

                // 가져온 어노테이션에서 특정 값을 가져옵니다.
                // 해당 값은 controller 어노테이션은 method, value이고, method 어노테이션은 value 입니다.
                String[] clazzUrls = clazzRequestMapping.value();
                HttpMethod[] httpMethods = clazzRequestMapping.method();
                String[] methodUrls = methodRequestMapping.value();

                // 가져온 값들을 카타시안 곱으로 조합하여 MethodIndicator 를 생성합니다.
                // MethodIndicator 는 http method 일치,  url 패턴매칭 여부를 판단합니다.
                List<MethodIndicator> methodIndicators = new ArrayList<>();
                for (HttpMethod httpMethod : httpMethods) {
                    for (String clazzUrl : clazzUrls) {
                        for (String methodUrl : methodUrls) {
                            methodIndicators.add(MethodIndicator.from(httpMethod, clazzUrl, methodUrl));
                        }
                    }
                }

                // n 개의 MethodIndicator 로 1 개의 MethodResolver 를 생성합니다.
                MethodResolver methodResolver = new MethodResolver(filteredClassMethods.getClass(), method, methodIndicators);
                // n 개의 MethodResolver 를 검색가능한 자료구조에 담습니다.
                methodResolvers.add(methodResolver);
            }
        }
    }
}