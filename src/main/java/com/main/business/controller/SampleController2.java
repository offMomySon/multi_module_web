package com.main.business.controller;

import annotation.Controller;
import annotation.PathVariable;
import annotation.RequestMapping;
import annotation.RequestParam;
import com.main.business.domain.SampleDomain1;
import lombok.extern.slf4j.Slf4j;
import matcher.RequestMethod;

@Slf4j
@Controller
@RequestMapping(value = "/basic2/{payMethod}")
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

    @RequestMapping(value = "/test/age", method = RequestMethod.GET)
    public String testMethod1(@RequestParam("id") String id, @RequestParam("age") Long _age) {
        log.info("id : `{}`, _age : `{}`", id, _age);
        return id + _age;
    }

    @RequestMapping(value = "/best", method = RequestMethod.GET)
    public String testMethod2(@RequestParam("test") ResponseDate ds) {
        return "result";
    }


    @RequestMapping(value = "/test/sambple", method = RequestMethod.POST)
    public ResponseDate<String> testMethod2() {
        return new ResponseDate<String>(200, "result");
    }
}
