package com.main.business.service;

import com.main.business.repository.SampleRepository;
import com.main.business.repository.SampleRepository2;
import com.main.container.annotation.Service;

@Service
public class SampleService {
    public final SampleRepository sampleRepository;
    public final SampleRepository2 sampleRepository2;

    public SampleService(SampleRepository sampleRepository, SampleRepository2 sampleRepository2) {
        this.sampleRepository = sampleRepository;
        this.sampleRepository2 = sampleRepository2;
    }
}
