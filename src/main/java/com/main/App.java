package com.main;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import mapper.AnnotationUtils;
import mapper.FileSystemClassFinder;
import mapper.MethodHandler;
import mapper.marker.Controller;
import mapper.marker.RequestMapping;

@Slf4j
public class App {

    public static void main(String[] args) {
        FileSystemClassFinder fileSystemClassFinder = FileSystemClassFinder.from(App.class, "com.main");
        List<? extends Class<?>> classes = fileSystemClassFinder.find();

        List<Class<?>> controllerClasses = classes.stream()
            .filter(clazz -> AnnotationUtils.find(clazz, Controller.class).isPresent())
            .filter(clazz -> AnnotationUtils.find(clazz, RequestMapping.class).isPresent())
            .collect(Collectors.toUnmodifiableList());

        List<MethodHandler> methodHandlers = new ArrayList<>();
        for (Class<?> clazz : controllerClasses) {
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if(!AnnotationUtils.find(method, RequestMapping.class).isPresent()){
                    continue;
                }

                MethodHandler methodHandler = MethodHandler.from(clazz, method);
                methodHandlers.add(methodHandler);
            }
        }

        methodHandlers
            .forEach(methodHandler -> log.info("methodHandler : {}", methodHandler));
    }
}