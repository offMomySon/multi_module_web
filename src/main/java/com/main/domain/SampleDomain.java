package com.main.domain;

import annotation.Domain;
import com.main.service.SampleService;

@Domain
public class SampleDomain {
    public final SampleService sampleService;

    public SampleDomain(SampleService sampleService) {
        this.sampleService = sampleService;
    }
}
