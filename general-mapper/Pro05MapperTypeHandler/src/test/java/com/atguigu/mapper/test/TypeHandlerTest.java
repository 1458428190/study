package com.atguigu.mapper.test;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.atguigu.mapper.entities.Address;
import com.atguigu.mapper.entities.SeasonEnum;
import com.atguigu.mapper.entities.User;
import com.atguigu.mapper.services.UserService;

public class TypeHandlerTest {
	
	private UserService userService;
	
	{
		userService = new ClassPathXmlApplicationContext("spring-context.xml").getBean(UserService.class);
	}
	
	@Test
	public void testQueryUser() {
		
		Integer userId = 8;
		
		User user = userService.getUserById(userId);
		
		System.out.println(user);
	}
	
	@Test
	public void testSaveUser() {
		
		User user = new User(null, "tom08", new Address("AAA", "BBB", "CCC"), SeasonEnum.AUTUMN);
		
		userService.saveUser(user);
		
	}

}
