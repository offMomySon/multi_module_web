package com.main.filter;

import filter.FilterWorker;
import filter.annotation.WebFilter;
import lombok.extern.slf4j.Slf4j;
import vo.HttpRequest;
import vo.HttpResponse;

@Slf4j
@WebFilter
public class LoggingFilterWorker implements FilterWorker {

    @Override
    public boolean prevExecute(HttpRequest httpRequest, HttpResponse httpResponse) {
        log.info("request method : {}", httpRequest.getHttpMethod());
        log.info("request uri : {}", httpRequest.getHttpRequestPath());
        log.info("request header : {}", httpRequest.getHttpHeader());
        log.info("request body : {}", httpRequest.getBodyString());
        return true;
    }

    @Override
    public boolean postExecute(HttpRequest httpRequest, HttpResponse httpResponse) {
        log.info("response startLine : {}", httpResponse.getStartLine());
        log.info("response headerMap : {}", httpResponse.getHeader());
        return true;
    }
}