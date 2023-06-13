package com.main;


import app.Application;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {
    public static void main(String[] args) {
        Application.run(App.class, "com.main");
    }
}