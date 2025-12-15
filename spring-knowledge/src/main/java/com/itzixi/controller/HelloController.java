package com.itzixi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName HelloController
 * @Author 陈壮壮
 * @Version 1.0
 * @Description HelloController
 **/
@RestController
@RequestMapping("hello")
public class HelloController {

    @GetMapping("world")
    public Object helloWorld() {
        return "Hello 陈壮壮~~~";
    }

}
