package com.main.business.service;

import com.main.business.repository.SampleRepository;
import container.annotation.Service;

@Service
public class SampleService2 {
    public final SampleRepository sampleRepository;

    public SampleService2(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }
}
