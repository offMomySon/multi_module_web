package com.main.controller;

import com.main.domain.SampleDomain;
import lombok.extern.slf4j.Slf4j;
import marker.Controller;
import marker.PathVariable;
import marker.RequestMapping;
import marker.RequestParam;
import vo.RequestMethod;

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
        log.info("id : `{}`, _age : `{}`");
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
