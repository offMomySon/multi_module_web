package com.main.filter;

import filter.FilterWorker2;
import filter.annotation.WebFilter;
import lombok.extern.slf4j.Slf4j;
import vo.HttpRequest;
import vo.HttpResponse;

@Slf4j
@WebFilter
public class LoggingFilterWorker2 implements FilterWorker2 {

    @Override
    public void prevExecute(HttpRequest httpRequest, HttpResponse httpResponse) {
        log.info("request method : {}", httpRequest.getHttpMethod());
        log.info("request uri : {}", httpRequest.getHttpUri());
        log.info("request header : {}", httpRequest.getHttpHeader());
        log.info("request body : {}", httpRequest.getBodyString());
    }

    @Override
    public void postExecute(HttpRequest httpRequest, HttpResponse httpResponse) {
        log.info("response startLine : {}", httpResponse.getStartLine());
        log.info("response headerMap : {}", httpResponse.getHeader());
    }
}
