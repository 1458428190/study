package com.gdufe.study.dubbo.service;

import com.gdufe.study.dubbo.bean.UserAddress;

import java.util.List;

/**
 * @Author: laichengfeng
 * @Description: 用户服务
 * @Date: 2019/1/2 16:02
 */
public interface UserService {

    List<UserAddress> getUserAddressList(String userId);
}
