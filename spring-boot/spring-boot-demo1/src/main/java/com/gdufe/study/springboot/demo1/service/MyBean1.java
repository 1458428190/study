package com.gdufe.study.springboot.demo1.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @Author: laichengfeng
 * @Description:
 * @Date: 2018/12/12 15:29
 */
@Component
public class MyBean1 implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        System.out.println("MyBeane1 : " + args);
    }
}
