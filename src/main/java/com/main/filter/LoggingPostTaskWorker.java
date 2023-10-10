package com.main.filter;

import annotation.PostWebFilter;
import lombok.extern.slf4j.Slf4j;
import task.PostTaskWorker;
import vo.HttpRequest;
import vo.HttpResponse;

@Slf4j
@PostWebFilter
public class LoggingPostTaskWorker implements PostTaskWorker {
    @Override
    public boolean execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        log.info("response startLine : {}", httpResponse.getStartLine());
        log.info("response headerMap : {}", httpResponse.getHeader());
        return true;
    }
}
