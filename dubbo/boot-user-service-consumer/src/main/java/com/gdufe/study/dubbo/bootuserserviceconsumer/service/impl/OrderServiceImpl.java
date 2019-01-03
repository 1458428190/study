package com.gdufe.study.dubbo.bootuserserviceconsumer.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gdufe.study.dubbo.bean.UserAddress;
import com.gdufe.study.dubbo.service.OrderService;
import com.gdufe.study.dubbo.service.UserService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: laichengfeng
 * @Description:
 * @Date: 2019/1/3 13:27
 */
@Service
public class OrderServiceImpl implements OrderService {

    /**
     * 设置负载均衡方式为轮训, 超时时间为1秒
     */
    @Reference(loadbalance = "random", timeout = 2000)
    UserService userService;

    @HystrixCommand(fallbackMethod = "hello")
    @Override
    public List<UserAddress> initOrder(String userId) {
        System.out.println("用户id："+userId);
        //1、查询用户的收货地址
        List<UserAddress> addressList = userService.getUserAddressList(userId);
        for (UserAddress userAddress : addressList) {
            System.out.println((userAddress.getPhoneName()));
        }
        return addressList;
    }

    /**
     * 服务容错
     * @param userId
     * @return
     */
    public List<UserAddress> hello(String userId) {
        // TODO Auto-generated method stub
        return Arrays.asList(new UserAddress("1", "1"));
    }
}
