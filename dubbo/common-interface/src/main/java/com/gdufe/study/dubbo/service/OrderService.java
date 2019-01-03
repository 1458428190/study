package com.gdufe.study.dubbo.service;

import com.gdufe.study.dubbo.bean.UserAddress;

import java.util.List;

/**
 * @Author: laichengfeng
 * @Description: 订单服务
 * @Date: 2019/1/2 16:04
 */
public interface OrderService {

    /**
     * 初始化订单
     * @param userId
     * @return
     */
    List<UserAddress> initOrder(String userId);
}
