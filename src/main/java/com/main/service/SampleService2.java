package com.main.service;

import annotation.Service;
import com.main.repository.SampleRepository;

@Service
public class SampleService2 {
    public final SampleRepository sampleRepository;

    public SampleService2(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }
}
