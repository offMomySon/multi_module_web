package com.main.service;

import com.main.repository.SampleRepository;
import com.main.repository.SampleRepository2;
import mapper.marker.Service;

@Service
public class SampleService {
    public final SampleRepository sampleRepository;
    public final SampleRepository2 sampleRepository2;

    public SampleService(SampleRepository sampleRepository, SampleRepository2 sampleRepository2) {
        this.sampleRepository = sampleRepository;
        this.sampleRepository2 = sampleRepository2;
    }
}
