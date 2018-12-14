package com.gdufe.study.springboot.demo1.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @Author: laichengfeng
 * @Description:
 * @Date: 2018/12/12 15:30
 */
//@Component
public class MyBean2 implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        System.out.println("MyBean2 : " + args);
    }
}
