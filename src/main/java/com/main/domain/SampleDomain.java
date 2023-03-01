package com.main.domain;

import com.main.service.SampleService;
import mapper.marker.Component;
import mapper.marker.Domain;

@Component
public class SampleDomain {
    public final SampleService sampleService;

    public SampleDomain(SampleService sampleService) {
        this.sampleService = sampleService;
    }
}
