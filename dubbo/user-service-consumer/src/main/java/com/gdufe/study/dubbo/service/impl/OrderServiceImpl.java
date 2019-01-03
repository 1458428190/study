package com.gdufe.study.dubbo.service.impl;

import com.gdufe.study.dubbo.bean.UserAddress;
import com.gdufe.study.dubbo.service.OrderService;
import com.gdufe.study.dubbo.service.UserService;

import java.util.List;

/**
 * @Author: laichengfeng
 * @Description:
 * @Date: 2019/1/2 16:55
 */
public class OrderServiceImpl implements OrderService {

    UserService userService;

    public OrderServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public List<UserAddress> initOrder(String userId) {
        // TODO Auto-generated method stub
        System.out.println("用户id："+userId);
        //1、查询用户的收货地址
        List<UserAddress> addressList = userService.getUserAddressList(userId);
        for (UserAddress userAddress : addressList) {
            System.out.println((userAddress.getPhoneName()));
        }
        return addressList;
     }
}
