package com.main;

import beanContainer.BeanContainerCreator;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import mapper.FileSystemUtil;
import mapper.MethodResolver;
import mapper.MethodResolverCreator;

@Slf4j
public class App {
    // url 에 매칭되는 method 를 출력하는 MethodResolver 를 만드는 과정이다.
    public static void main(String[] args) {
        // 파일시스템에서 지정한 패키지 하위의 모든 클래스 파일을 가져옵니다.
        // 가져온 이유는 클래스의 메소드를 객체화 하기 위해서 입니다.
        List<Class<?>> classes = FileSystemUtil.findClass(App.class, "com.main");

        MethodResolverCreator methodResolverCreator = new MethodResolverCreator();
        MethodResolver methodResolver = methodResolverCreator.create(classes);

        BeanContainerCreator beanContainerCreator = new BeanContainerCreator();
        try {
            Map<Class<?>, Object> classObjectMap = beanContainerCreator.create(classes);

            classObjectMap.entrySet().stream()
                .forEach(e -> log.info("class : `{}`, obj : `{}`", e.getKey(), e.getValue()));

        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}