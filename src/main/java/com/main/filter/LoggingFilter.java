package com.main.filter;

import annotation.WebFilter;
import filter.FilterChain;
import filter.OrderFilter;
import lombok.extern.slf4j.Slf4j;
import vo.HttpRequest;
import vo.HttpResponse;

@Slf4j
@WebFilter
public class LoggingFilter extends OrderFilter {

    @Override
    public boolean isPossibleFilterPattern(String pathUrl) {
        return true;
    }

    @Override
    public void doChain(HttpRequest request, HttpResponse response, FilterChain chain) {
        log.info("request method : {}", request.getHttpMethod());
        log.info("request uri : {}", request.getHttpUri());
        log.info("request header : {}", request.getHttpHeader());
        log.info("request body : {}", request.getBodyString());

        chain.doChain(request, response);

        log.info("response startLine : {}", response.getStartLine());
        log.info("response headerMap : {}", response.getHeader());
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
