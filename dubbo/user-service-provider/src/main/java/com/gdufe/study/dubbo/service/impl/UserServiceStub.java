package com.gdufe.study.dubbo.service.impl;

import com.gdufe.study.dubbo.bean.UserAddress;
import com.gdufe.study.dubbo.service.UserService;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @Author: laichengfeng
 * @Description:
 * @Date: 2019/1/2 16:39
 */
public class UserServiceStub implements UserService {

    private final UserService userService;

    /**
     * 传入的是userService远程的代理对象
     * @param userService
     */
    public UserServiceStub(UserService userService) {
        super();
        this.userService = userService;
    }

    @Override
    public List<UserAddress> getUserAddressList(String userId) {
        System.out.println("UserServiceStub.......");
        if(!StringUtils.isEmpty(userId)) {
            return userService.getUserAddressList(userId);
        }
        return null;
    }
}
