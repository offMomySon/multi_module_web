package com.main;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {
    private static final String CONTROLLER_PACKAGE = "com.main.controller";

    public static void main(String[] args) throws URISyntaxException, IOException {

//        App.class.getProtectionDomain().get

        URI uri = App.class.getResource("").toURI();
        System.out.println("uri : " + uri);
        Path myPath;
        if (uri.getScheme().equals("jar")) {
            FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
            Path path = fileSystem.getPath("");
            System.out.println("path : " + path);
            myPath = fileSystem.getPath("");
        } else {
            myPath = Paths.get(uri);
        }
        Stream<Path> walk = Files.walk(myPath, 3);
        for (Iterator<Path> it = walk.iterator(); it.hasNext();){
            System.out.println(it.next());
        }

//        TaskRegister taskRegister = TaskRegister.registerTaskMapper(App.class, CONTROLLER_PACKAGE);
//        List<TaskActuator> taskActuators = taskRegister.getTaskActuators();
//        log.info("taskMapper : {}", taskActuators);
//
//        HttpService httpService = new HttpService(App.class, CONTROLLER_PACKAGE);
//        httpService.start();
    }
}