package com.main.task.response;

import java.text.SimpleDateFormat;

public class BaseHttpResponseHeaderCreator extends HttpResponseHeaderCreator {

    public BaseHttpResponseHeaderCreator(SimpleDateFormat simpleDateFormat, String hostAddress) {
        super(simpleDateFormat, hostAddress);
    }

    @Override
    public String extractContentType() {
        return null;
    }
}


/*
 * 1. controller
 *  1.1. RestController
 * 2. method
 *  2.1. ResponseBody
 *  2.2. None ResponseBody
 *
 *
 */