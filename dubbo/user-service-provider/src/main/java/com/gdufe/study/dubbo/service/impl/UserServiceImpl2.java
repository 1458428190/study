package com.gdufe.study.dubbo.service.impl;

import com.gdufe.study.dubbo.bean.UserAddress;
import com.gdufe.study.dubbo.service.UserService;

import java.util.Arrays;
import java.util.List;

public class UserServiceImpl2 implements UserService {

	@Override
	public List<UserAddress> getUserAddressList(String userId) {
		System.out.println("UserServiceImpl.....new...");
		UserAddress address1 = new UserAddress("1", "010-56253825");
		UserAddress address2 = new UserAddress("2", "010-56253825");
		
		return Arrays.asList(address1,address2);
	}

}
