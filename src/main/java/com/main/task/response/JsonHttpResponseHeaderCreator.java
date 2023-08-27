package com.main.task.response;

import java.text.SimpleDateFormat;

public class JsonHttpResponseHeaderCreator extends HttpResponseHeaderCreator {

    public JsonHttpResponseHeaderCreator(SimpleDateFormat simpleDateFormat, String hostAddress) {
        super(simpleDateFormat, hostAddress);
    }

    @Override
    public String extractContentType() {
        return "application/json";
    }
}
