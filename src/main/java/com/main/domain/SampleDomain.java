package com.main.domain;

import com.main.service.SampleService;
import marker.Domain;

@Domain
public class SampleDomain {
    public final SampleService sampleService;

    public SampleDomain(SampleService sampleService) {
        this.sampleService = sampleService;
    }
}
