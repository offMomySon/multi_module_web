package com.main;


import app.Application;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {
    public static void main(String[] args) {
        Application.run(App.class, "com.main", "resources");
//        FileFinder fileFinder = FileFinder.create(App.class, "resources");
//        Optional<Path> resources = fileFinder.findResource(Path.of("result.txt"));
//
//        if (resources.isEmpty()) {
//            log.info("doesNot exist");
//        }
//
//        Path filePath = resources.get();
//        log.info(filePath.toString());
//
//        try (BufferedOutputStream reader = new BufferedOutputStream(new FileOutputStream(filePath.toFile()))) {
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}