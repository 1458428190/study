package com.atguigu.mapper.test;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MapperTest {
	
	private ApplicationContext iocContainer = new ClassPathXmlApplicationContext("spring-context.xml");
	
	@Test
	public void testDataSource() throws SQLException {
		DataSource dataSource = iocContainer.getBean(DataSource.class);
		Connection connection = dataSource.getConnection();
		System.out.println(connection);
	}
	
}
