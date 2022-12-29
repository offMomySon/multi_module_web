package com.main;

import lombok.extern.slf4j.Slf4j;
import mapper.TaskMapper;
import mapper.TaskRegister;
import processor.HttpService;

@Slf4j
public class App {
    private static final String CONTROLLER_PACKAGE = "com.main.controller";

    public static void main(String[] args) {
        TaskRegister taskRegister = TaskRegister.registerTaskMapper(App.class, CONTROLLER_PACKAGE);
        TaskMapper taskMapper = taskRegister.getTaskMapper();

        log.info("taskMapper : {}", taskMapper);

        HttpService httpService = new HttpService(App.class, CONTROLLER_PACKAGE);
        httpService.start();
    }
}