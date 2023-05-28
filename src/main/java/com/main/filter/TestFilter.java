package com.main.filter;

import filter.FilterChain;
import filter.OrderFilter;
import filter.WebFilter;
import lombok.extern.slf4j.Slf4j;
import vo.HttpRequest;
import vo.HttpResponse;

@Slf4j
@WebFilter
public class TestFilter extends OrderFilter {
    @Override
    public boolean isPossibleFilterPattern(String pathUrl) {
        return false;
    }

    @Override
    public void doChain(HttpRequest request, HttpResponse response, FilterChain chain) {
        log.info("start test filter");

        chain.doChain(request, response);

        log.info("end test filter");
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
