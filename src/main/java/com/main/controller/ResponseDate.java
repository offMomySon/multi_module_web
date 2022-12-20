package com.main.controller;

public class ResponseDate<T> {
    private final int code;
    private final T data;

    public ResponseDate(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public T getData() {
        return data;
    }
}
