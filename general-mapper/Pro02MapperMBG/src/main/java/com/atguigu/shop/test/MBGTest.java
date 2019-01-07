package com.atguigu.shop.test;

import java.io.InputStream;
import java.util.Iterator;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.atguigu.shop.entities.Employee;
import com.atguigu.shop.mappers.EmployeeMapper;

import tk.mybatis.mapper.mapperhelper.MapperHelper;

public class MBGTest {
	
	public static void main(String[] args) {
		
		SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
		
		InputStream inputStream = MBGTest.class.getClassLoader().getResourceAsStream("mybatis-config.xml");
		
		SqlSessionFactory factory = builder.build(inputStream);
		
		SqlSession session = factory.openSession();
		
		//***********************
		//按照Java方式整合通用Mapper的特殊设置
		//i.创建MapperHelper对象
		MapperHelper mapperHelper = new MapperHelper();
		
		//ii.通过MapperHelper对象对MyBatis原生的Configuration对象进行处理
		Configuration configuration = session.getConfiguration();
		mapperHelper.processConfiguration(configuration);
		
		//***********************
		
		EmployeeMapper mapper = session.getMapper(EmployeeMapper.class);
		
		Iterator<Employee> iterator = mapper.selectAll().iterator();
		
		while (iterator.hasNext()) {
			Employee employee = (Employee) iterator.next();
			System.out.println(employee);
		}
		
	}

}
