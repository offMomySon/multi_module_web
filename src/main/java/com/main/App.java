package com.main;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import mapper.FileSystemClassFinder;
import mapper.MethodHandler;
import mapper.MethodHandlerRegister;

@Slf4j
public class App {

    public static void main(String[] args) {
        FileSystemClassFinder fileSystemClassFinder = FileSystemClassFinder.from(App.class, "com.main");
        List<? extends Class<?>> clazzes = fileSystemClassFinder.find();

        MethodHandlerRegister methodHandlerRegister = MethodHandlerRegister.register(clazzes);
        List<MethodHandler> methodHandlers = methodHandlerRegister.getMethodHandlers();

        methodHandlers.forEach(methodHandler -> log.info("MethodHandler : {}", methodHandler));
    }
}