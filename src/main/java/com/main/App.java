package com.main;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import mapper.ClassFinder;

@Slf4j
public class App {

    public static void main(String[] args) throws URISyntaxException, IOException {
        ClassFinder classFinder = ClassFinder.from(App.class, "com.main");
        List<? extends Class<?>> clazzes = classFinder.findFromSystem();


    }
}