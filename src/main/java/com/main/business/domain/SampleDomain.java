package com.main.business.domain;

import com.main.business.service.SampleService;
import com.main.container.annotation.Domain;

@Domain
public class SampleDomain {
    public final SampleService sampleService;

    public SampleDomain(SampleService sampleService) {
        this.sampleService = sampleService;
    }
}
