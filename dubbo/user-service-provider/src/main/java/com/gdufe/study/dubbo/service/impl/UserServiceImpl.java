package com.gdufe.study.dubbo.service.impl;

import com.gdufe.study.dubbo.bean.UserAddress;
import com.gdufe.study.dubbo.service.UserService;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: laichengfeng
 * @Description:
 * @Date: 2019/1/2 16:23
 */
public class UserServiceImpl implements UserService {

    @Override
    public List<UserAddress> getUserAddressList(String userId) {
        System.out.println("UserServiceImpl ... old ...");
        UserAddress userAddress1 = new UserAddress("1", "1");
        UserAddress userAddress2 = new UserAddress("2", "2");
        return Arrays.asList(userAddress1, userAddress2);
    }
}
