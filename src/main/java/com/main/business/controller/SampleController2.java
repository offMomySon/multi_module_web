package com.main.business.controller;

import com.main.business.domain.SampleDomain1;
import matcher.RequestMethod;
import annotation.PathVariable;
import annotation.RequestMapping;
import annotation.RequestParam;
import annotation.Controller;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SampleController2 {
    public final SampleDomain1 sampleDomain1;

    public SampleController2(SampleDomain1 sampleDomain1) {
        this.sampleDomain1 = sampleDomain1;
    }

    @RequestMapping(url = "/basic2/{pathParam}")
    public String testMethod(@PathVariable(name = "pathParam") String testParam) {
        log.info("testParam : {}", testParam);
        return "testParam";
    }

    @RequestMapping(url = "/basic2/test/age", method = RequestMethod.GET)
    public String testMethod1(@RequestParam(name = "id") String id, @RequestParam(name = "age") Long _age) {
        log.info("id : `{}`, _age : `{}`", id, _age);
        return id + _age;
    }

    @RequestMapping(url = "/basic2/best", method = RequestMethod.GET)
    public String testMethod2(@RequestParam(name = "test") ResponseDate ds) {
        return "result";
    }


    @RequestMapping(url = "/basic2/test/sambple", method = RequestMethod.POST)
    public ResponseDate<String> testMethod2() {
        return new ResponseDate<String>(200, "result");
    }
}
