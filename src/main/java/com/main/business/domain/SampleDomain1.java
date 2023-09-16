package com.main.business.domain;

import annotation.Domain;
import com.main.business.repository.SampleRepository;
import com.main.business.service.SampleService2;

@Domain
public class SampleDomain1 {
    public final SampleService2 sampleService2;
    public final SampleRepository sampleRepository;

    public SampleDomain1(SampleService2 sampleService2, SampleRepository sampleRepository) {
        this.sampleService2 = sampleService2;
        this.sampleRepository = sampleRepository;
    }
}
