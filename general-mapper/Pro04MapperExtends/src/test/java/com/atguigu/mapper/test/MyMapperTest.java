package com.atguigu.mapper.test;

import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.atguigu.mapper.entities.Employee;
import com.atguigu.mapper.services.EmployeeService;

public class MyMapperTest {
	
	public static void main(String[] args) {
		
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-context.xml");
		
		EmployeeService employeeService = context.getBean(EmployeeService.class);
		
		List<Employee> empList = employeeService.getAll();
		for (Employee employee : empList) {
			System.out.println(employee);
		}
		
		context.close();
		
	}

}
