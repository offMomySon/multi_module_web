package com.main.task.response;

import java.text.SimpleDateFormat;

public class TextHttpResponseHeaderCreator extends HttpResponseHeaderCreator {

    public TextHttpResponseHeaderCreator(SimpleDateFormat simpleDateFormat, String hostAddress) {
        super(simpleDateFormat, hostAddress);
    }

    @Override
    public String extractContentType() {
        return "text/html; charset=UTF-8";
    }
}
