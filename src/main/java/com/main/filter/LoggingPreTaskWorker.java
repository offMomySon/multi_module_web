package com.main.filter;

import annotation.PreWebFilter;
import lombok.extern.slf4j.Slf4j;
import pretask.PreTaskWorker;
import vo.HttpRequest;
import vo.HttpResponse;

@Slf4j
@PreWebFilter
public class LoggingPreTaskWorker implements PreTaskWorker {

    @Override
    public boolean execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        log.info("request method : {}", httpRequest.getHttpMethod());
        log.info("request uri : {}", httpRequest.getHttpRequestPath());
        log.info("request header : {}", httpRequest.getHttpHeader());
        log.info("request body : {}", httpRequest.getBodyString());
        return true;
    }
}
