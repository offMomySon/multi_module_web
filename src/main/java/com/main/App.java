package com.main;

import lombok.extern.slf4j.Slf4j;
import mapper.TaskMapper;
import processor.HttpService;

@Slf4j
public class App {
    private static final String CONTROLLER_PACKAGE = "com.main.controller";

    public static void main(String[] args) {
        TaskMapper taskMapper = TaskMapper.create(App.class, CONTROLLER_PACKAGE);

        HttpService httpService = new HttpService();
        httpService.start();
    }
}