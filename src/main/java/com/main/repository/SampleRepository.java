package com.main.repository;

import com.main.service.SampleService;
import mapper.marker.Repository;

@Repository
public class SampleRepository {
    private final SampleService sampleService;

    public SampleRepository(SampleService sampleService) {
        this.sampleService = sampleService;
    }
}
