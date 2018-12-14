package com.gdufe.study.springboot.demo1.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: laichengfeng
 * @Description:
 * @Date: 2018/12/5 11:42
 */
@RestController
@EnableAutoConfiguration
public class Example {

    @RequestMapping("/test")
    String home() {
        return "Hello world!!!!!!-";
    }

    public static void main(String[] args) {
        SpringApplication.run(Example.class, args);
    }

}
