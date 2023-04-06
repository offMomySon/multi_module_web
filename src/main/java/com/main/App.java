package com.main;

import beanContainer.ComponentContainer;
import beanContainer.ComponentContainerCreator;
import executor.RequestExecutor;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import mapper.ControllerPathMatcherCreator;
import mapper.FileSystemUtil;
import mapper.HttpPathMatcherIf;
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

        ComponentContainer componentContainer = new ComponentContainerCreator(classes).create();
        HttpPathMatcherIf httpPathMatcher = new ControllerPathMatcherCreator(classes).create();

        RequestExecutor requestExecutor = new RequestExecutor(componentContainer, httpPathMatcher);
        Object result = requestExecutor.execute(RequestMethod.GET, "/basic/pathVariable", RequestParameters.empty(), RequestBodyContent.empty());

        System.out.println(result);
    }
}