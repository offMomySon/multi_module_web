package com.main.business.controller;

import com.main.business.domain.SampleDomain;
import annotation.Controller;
import lombok.extern.slf4j.Slf4j;
import matcher.RequestMethod;
import annotation.PathVariable;
import annotation.RequestMapping;
import annotation.RequestParam;


@Slf4j
@Controller
public class SampleController {
    public final SampleDomain sampleDomain;

    public SampleController(SampleDomain sampleDomain) {
        this.sampleDomain = sampleDomain;
    }

    @RequestMapping(url = "/basic/{pathParam}")
    public String testMethod(@PathVariable(name = "pathParam") String testParam) {
        log.info("testParam : {}", testParam);
        return "testParam";
    }

    @RequestMapping(url = "/basic/test/age", method = RequestMethod.GET)
    public String testMethod1(@RequestParam(name = "id") String id, @RequestParam(name = "age") Long _age) {
        log.info("id : `{}`, _age : `{}`", id, _age);
        return id + _age;
    }

    @RequestMapping(url = "/basic/best", method = RequestMethod.GET)
    public String testMethod2(@RequestParam(name = "test") ResponseDate ds) {
        return "result";
    }


    @RequestMapping(url = "/basic/test/sambple", method = RequestMethod.POST)
    public ResponseDate<String> testMethod2() {
        return new ResponseDate<String>(200, "result");
    }
}
