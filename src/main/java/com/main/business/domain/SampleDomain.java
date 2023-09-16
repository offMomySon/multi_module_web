package com.main.business.domain;

import annotation.Domain;
import com.main.business.service.SampleService;

@Domain
public class SampleDomain {
    public final SampleService sampleService;

    public SampleDomain(SampleService sampleService) {
        this.sampleService = sampleService;
    }
}
