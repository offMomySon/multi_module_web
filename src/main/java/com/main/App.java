package com.main;

import beanContainer.ComponentClassLoader;
import executor.MethodExecutor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import mapper.AnnotationUtils;
import mapper.CompositedHttpPathMatcher;
import mapper.FileSystemUtil;
import mapper.HttpPathMatcher;
import mapper.HttpPathMatcher.MatchedMethod;
import mapper.JavaMethodResolverCreator;
import marker.Component;
import marker.Controller;
import variableExtractor.MethodConverter;
import variableExtractor.ParameterConverterFactory;
import vo.RequestBodyContent;
import vo.RequestMethod;
import vo.RequestParameters;

@Slf4j
public class App {
    // url 에 매칭되는 method 를 출력하는 MethodResolver 를 만드는 과정이다.
    public static void main(String[] args) {
        // 파일시스템에서 지정한 패키지 하위의 모든 클래스 파일을 가져옵니다.
        // 가져온 이유는 클래스의 메소드를 객체화 하기 위해서 입니다.
        List<Class<?>> classes = FileSystemUtil.findClass(App.class, "com.main");

        List<Class<?>> controllerClazzs = classes.stream()
            .filter(clazz -> AnnotationUtils.exist(clazz, Controller.class))
            .collect(Collectors.toUnmodifiableList());

        List<HttpPathMatcher> httpPathMatchers = controllerClazzs.stream()
            .map(JavaMethodResolverCreator::new)
            .map(JavaMethodResolverCreator::create)
            .flatMap(Collection::stream)
            .peek(httpPathMatcher -> log.info("httpPathMatcher : `{}`", httpPathMatcher))
            .collect(Collectors.toUnmodifiableList());

        List<Class<?>> componentClasses = classes.stream()
            .filter(clazz -> AnnotationUtils.exist(clazz, Component.class))
            .collect(Collectors.toUnmodifiableList());

        List<ComponentClassLoader> componentClassLoaders = componentClasses.stream()
            .map(ComponentClassLoader::new)
            .collect(Collectors.toUnmodifiableList());

        Map<Class<?>, Object> container = new HashMap<>();
        for (ComponentClassLoader classLoader : componentClassLoaders) {
            Map<Class<?>, Object> newContainer = classLoader.load(container);
            newContainer.forEach((key, value) -> container.merge(key, value, (prev, curr) -> prev));
        }
        container.forEach((key, value) -> log.info("class : `{}`, obj : `{}`", key, value));

        CompositedHttpPathMatcher httpPathMatcher = new CompositedHttpPathMatcher(httpPathMatchers);
        MatchedMethod matchedMethod = httpPathMatcher.matchMethod(RequestMethod.GET, "/basic/pathVariable")
            .orElseThrow(() -> new RuntimeException(""));

        Map<String, String> pathVariable = matchedMethod.getPathVariable();

        ParameterConverterFactory converterFactory = new ParameterConverterFactory(RequestParameters.empty(), new RequestParameters(pathVariable), RequestBodyContent.empty());
        MethodConverter converter = new MethodConverter(converterFactory);

        Method javaMethod = matchedMethod.getJavaMethod();
        MethodExecutor methodExecutor = new MethodExecutor(container, converter);
        Object result = methodExecutor.execute(javaMethod);
        System.out.println(result);
    }
}