package com.gdufe.study.sofaboot.service.provider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.gdufe.study.sofaboot.service.facade.SampleJvmService;

/**
 * @author: laichengfeng
 * @description: JVM发布服务, XML方式
 * @Date: 2018/10/10 14:36
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SampleJvmServiceImpl implements SampleJvmService {

    private String message;

    public String message() {
        System.out.println(message);
        return message;
    }
}