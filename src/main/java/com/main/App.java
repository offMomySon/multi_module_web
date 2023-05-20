package com.main;

import com.main.executor.MethodExecutor;
import com.main.executor.RequestExecutor;
import container.ComponentContainerCreator;
import container.Container;
import converter.CompositeConverter;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import method.HttpPathMatcher;
import method.support.ControllerPathMatcherCreator;
import processor.HttpService;
import util.FileSystemUtil;

@Slf4j
public class App {
    // url 에 매칭되는 method 를 출력하는 MethodResolver 를 만드는 과정이다.
    public static void main(String[] args) {
        // 파일시스템에서 지정한 패키지 하위의 모든 클래스 파일을 가져옵니다.
        // 가져온 이유는 클래스의 메소드를 객체화 하기 위해서 입니다.
        List<Class<?>> classes = FileSystemUtil.findClass(App.class, "com.main");

        Container container = new ComponentContainerCreator(classes).create();
        MethodExecutor methodExecutor = new MethodExecutor(container);
        HttpPathMatcher httpPathMatcher = new ControllerPathMatcherCreator(classes).create();
        CompositeConverter converter = new CompositeConverter();
        RequestExecutor requestExecutor = new RequestExecutor(methodExecutor, httpPathMatcher, converter);

        HttpService httpService = new HttpService(requestExecutor);
        httpService.start();
    }
}