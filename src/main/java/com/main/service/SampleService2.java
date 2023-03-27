package com.main.service;

import com.main.repository.SampleRepository;
import marker.Service;

@Service
public class SampleService2 {
    public final SampleRepository sampleRepository;

    public SampleService2(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }
}
