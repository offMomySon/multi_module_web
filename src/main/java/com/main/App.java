package com.main;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import mapper.AnnotationUtils;
import mapper.ClassHandler;
import mapper.FileSystemClassFinder;
import mapper.marker.Controller;

@Slf4j
public class App {

    public static void main(String[] args) {
        FileSystemClassFinder fileSystemClassFinder = FileSystemClassFinder.from(App.class, "com.main");
        List<? extends Class<?>> classes = fileSystemClassFinder.find();

        List<Class<?>> controllerClasses = classes.stream()
            .filter(clazz -> AnnotationUtils.find(clazz, Controller.class).isPresent())
            .collect(Collectors.toUnmodifiableList());

        List<ClassHandler> classHandlers = controllerClasses.stream()
            .map(ClassHandler::from)
            .collect(Collectors.toUnmodifiableList());

        classHandlers
            .forEach(classHandler -> log.info("ClassHandlers : {}", classHandler));
    }
}