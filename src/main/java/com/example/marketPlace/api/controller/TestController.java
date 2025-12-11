package com.example.marketPlace.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@Api(tags = "API", value = "")
@RequestMapping("/public")
public class TestController {


    @GetMapping
    public String hello() {
        return "Hello World ";
    }

}
