package com.main.business.controller;

import com.main.business.domain.SampleDomain;
import container.annotation.Controller;
import lombok.extern.slf4j.Slf4j;
import matcher.RequestMethod;
import matcher.annotation.PathVariable;
import matcher.annotation.RequestMapping;
import matcher.annotation.RequestParam;


@Slf4j
@Controller
@RequestMapping(value = "/basic")
public class SampleController {
    public final SampleDomain sampleDomain;

    public SampleController(SampleDomain sampleDomain) {
        this.sampleDomain = sampleDomain;
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
