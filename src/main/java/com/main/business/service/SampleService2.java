package com.main.business.service;

import annotation.Service;
import com.main.business.repository.SampleRepository;

@Service
public class SampleService2 {
    public final SampleRepository sampleRepository;

    public SampleService2(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }
}
