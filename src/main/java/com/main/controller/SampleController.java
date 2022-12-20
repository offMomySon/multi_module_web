package com.main.controller;

import lombok.extern.slf4j.Slf4j;
import mapper.marker.PathVariable;
import mapper.marker.RequestMapping;
import mapper.marker.RequestParam;
import vo.HttpMethod;

@Slf4j
@RequestMapping("/{paymentPath}/payletter/{payMethod}")
public class SampleController {

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

    @RequestMapping(value = "/test/sambple", method = HttpMethod.POST)
    public ResponseDate<String> testMethod2() {
        return new ResponseDate<String>(200, "result");
    }
}
