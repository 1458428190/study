package com.gdufe.study.dubbo.bootuserserviceprovider.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gdufe.study.dubbo.bean.UserAddress;
import com.gdufe.study.dubbo.service.UserService;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: laichengfeng
 * @Description:
 * @Date: 2019/1/3 13:10
 */
// 注解形式暴露服务
@Service
@Component
public class UserServiceImpl implements UserService {

//    @HystrixCommand
    @Override
    public List<UserAddress> getUserAddressList(String userId) {
        System.out.println("UserServiceImpl....3....");
        UserAddress userAddress1 = new UserAddress("3", "4");
        UserAddress userAddress2 = new UserAddress("4", "4");
        /**
         * 随机异常
         */
        if(Math.random() >= 0.5) {
            throw new RuntimeException();
        }
        return Arrays.asList(userAddress1, userAddress2);
    }
}
