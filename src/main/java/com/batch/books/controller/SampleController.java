package com.batch.books.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class SampleController {

    @RequestMapping("/map")
    public String map(@RequestParam("bar") String foo, @RequestParam("foo") String bar){
        return bar + foo;
    }
//    public String map(@RequestBody SampleObject sampleObject){
//        return sampleObject.getB() + sampleObject.getC();
//    }

    public class SampleObject{
        String b;
        String c;

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }

        public String getC() {
            return c;
        }

        public void setC(String c) {
            this.c = c;
        }
    }
}
