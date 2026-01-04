package com.example.marketPlace.api.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@Api(tags = "API", value = "")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api")
public class TestController {



    @GetMapping("/test1")
    @PreAuthorize("hasAuthority('api.marketplace.api.test1')")
    public String hello1() {
        return "Hello World 1 ";
    }

    @GetMapping("/test2")
    @PreAuthorize("hasAuthority('api.marketplace.api.test2')")
    public String hello2() {
        return "Hello World 2";
    }

    @GetMapping("/test3")
    @PreAuthorize("hasAuthority('api.marketplace.api.test3')")
    public String hello3() {
        return "Hello World 3";
    }

}
