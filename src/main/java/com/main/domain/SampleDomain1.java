package com.main.domain;

import com.main.repository.SampleRepository;
import com.main.service.SampleService2;
import mapper.marker.Domain;

@Domain
public class SampleDomain1 {
    public final SampleService2 sampleService2;
    public final SampleRepository sampleRepository;

    public SampleDomain1(SampleService2 sampleService2, SampleRepository sampleRepository) {
        this.sampleService2 = sampleService2;
        this.sampleRepository = sampleRepository;
    }
}
