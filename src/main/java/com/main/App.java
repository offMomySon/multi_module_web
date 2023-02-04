package com.main;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import mapper.FileSystemClassFinder;

@Slf4j
public class App {

    public static void main(String[] args) throws URISyntaxException, IOException {
        FileSystemClassFinder fileSystemClassFinder = FileSystemClassFinder.from(App.class, "com.main");
        List<? extends Class<?>> clazzes = fileSystemClassFinder.find();

    }
}