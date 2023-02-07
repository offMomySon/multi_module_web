package com.main;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import mapper.AnnotationUtils;
import mapper.FileSystemClassFinder;
import mapper.MethodHandlerRepository;
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

        MethodHandlerRepository methodHandlerRepository = MethodHandlerRepository.from(controllerClasses);
    }
}