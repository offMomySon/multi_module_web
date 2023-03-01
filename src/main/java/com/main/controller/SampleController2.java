package com.main.controller;

import com.main.domain.SampleDomain1;
import lombok.extern.slf4j.Slf4j;
import mapper.marker.Component;
import mapper.marker.Controller;
import mapper.marker.PathVariable;
import mapper.marker.RequestMapping;
import mapper.marker.RequestParam;
import vo.HttpMethod;

@Slf4j
@Component
@Controller
@RequestMapping(value = "/basic/{payMethod}")
public class SampleController2 {
    public final SampleDomain1 sampleDomain1;

    public SampleController2(SampleDomain1 sampleDomain1) {
        this.sampleDomain1 = sampleDomain1;
    }

    @RequestMapping(value = "/{pathParam}")
    public String testMethod(@PathVariable(value = "pathParam") String testParam) {
        log.info("testParam : {}", testParam);
        return "testParam";
    }

    @RequestMapping(value = "/test/age", method = HttpMethod.GET)
    public String testMethod1(@RequestParam("id") String id, @RequestParam("age") Long _age) {
        log.info("id : `{}`, _age : `{}`");
        return id + _age;
    }

    @RequestMapping(value = "/best", method = HttpMethod.GET)
    public String testMethod2(@RequestParam("test") ResponseDate ds) {
        return "result";
    }


    @RequestMapping(value = "/test/sambple", method = HttpMethod.POST)
    public ResponseDate<String> testMethod2() {
        return new ResponseDate<String>(200, "result");
    }
}
