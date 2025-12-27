package com.example.marketPlace.api.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@Api(tags = "API", value = "")
@RefreshScope
@RequestMapping("/public")
public class PublicTestController {


    @Value("${my.value.myName}")
    private String myName;

    @GetMapping("/test1")
    public String hello1() {
        return "Hello World 1 "+myName;
    }

    @GetMapping("/test2")
    public String hello2() {
        return "Hello World 2";
    }

    @GetMapping("/test3")
    public String hello3() {
        return "Hello World 3";
    }

}
