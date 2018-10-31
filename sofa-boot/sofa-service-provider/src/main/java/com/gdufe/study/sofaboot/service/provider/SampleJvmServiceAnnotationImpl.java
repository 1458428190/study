package com.gdufe.study.sofaboot.service.provider;

import com.gdufe.study.sofaboot.service.facade.SampleJvmService;
import com.alipay.sofa.runtime.api.annotation.SofaService;

/**
 * @author: laichengfeng
 * @description: JVM发布服务, 注解方式
 * @Date: 2018/10/10 14:55
 */
@SofaService(uniqueId="annotionImpl")
public class SampleJvmServiceAnnotationImpl implements SampleJvmService {

    public String message() {
        String message = "Hello, laichengfeng, jvm service annotation implementation.";
        System.out.println(message);
        return message;
    }
}