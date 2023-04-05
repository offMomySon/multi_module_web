package com.main;

import beanContainer.BeanContainer;
import beanContainer.BeanContainerCreator;
import executor.MethodExecutor;
import java.lang.reflect.Method;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import mapper.FileSystemUtil;
import mapper.HttpPathMatcher.MatchedMethod;
import mapper.HttpPathMatcherCreator;
import mapper.HttpPathMatcherIf;
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

        BeanContainer beanContainer = new BeanContainerCreator(classes).create();
        HttpPathMatcherIf httpPathMatcher = new HttpPathMatcherCreator(classes).create();

        MatchedMethod matchedMethod = httpPathMatcher.matchMethod(RequestMethod.GET, "/basic/pathVariable").orElseThrow(() -> new RuntimeException(""));
        Method javaMethod = matchedMethod.getJavaMethod();

        ParameterConverterFactory converterFactory = new ParameterConverterFactory(RequestParameters.empty(), new RequestParameters(matchedMethod.getPathVariable()), RequestBodyContent.empty());
        MethodConverter converter = new MethodConverter(converterFactory);
        MethodExecutor methodExecutor = new MethodExecutor(beanContainer, converter);

        Object result = methodExecutor.execute(javaMethod);
        System.out.println(result);
    }
}