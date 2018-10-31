/**
 * @(#)SofaBootWithModulesTest.java, 2018/10/10.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.gdufe.study.sofaboot.test.run;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.gdufe.study.sofaboot.service.facade.SampleJvmService;

/**
 * @author: laichengfeng (laichengfeng @ corp.netease.com)
 * @description:
 * @Date: 2018/10/10 16:05
 */
@SpringBootTest
//@RunWith(SpringRunner.class)
public class SofaBootWithModulesTest {

    @SofaReference
    private SampleJvmService sampleJvmService;


}