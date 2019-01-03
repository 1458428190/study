package com.gdufe.study.dubbo.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: laichengfeng
 * @Description:
 * @Date: 2019/1/2 15:59
 */
@Data
@AllArgsConstructor
public class UserAddress implements Serializable {

    private String userId;
    private String phoneName;
}
