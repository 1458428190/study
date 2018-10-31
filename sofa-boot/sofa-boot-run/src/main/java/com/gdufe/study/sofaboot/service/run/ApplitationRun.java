/**
 * @(#)ApplitationRun.java, 2018/10/10.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.gdufe.study.sofaboot.service.run;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: laichengfeng (laichengfeng @ corp.netease.com)
 * @description:
 * @Date: 2018/10/10 15:37
 */
@SpringBootApplication
public class ApplitationRun {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ApplitationRun.class);
        springApplication.run(args);
    }
}