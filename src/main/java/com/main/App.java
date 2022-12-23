package com.main;

import lombok.extern.slf4j.Slf4j;
import processor.HttpService;

@Slf4j
public class App {
    public static void main(String[] args) {
        HttpService httpService = new HttpService();
        httpService.start();
    }
}