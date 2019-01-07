package com.atguigu.mapper.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atguigu.mapper.entities.Employee;
import com.atguigu.mapper.mappers.EmployeeMapper;

@Service
public class EmployeeService {
	
	@Autowired
	private EmployeeMapper employeeMapper;

	public List<Employee> getAll() {
		return employeeMapper.selectAll();
	}

	public void batchUpdateEmp(List<Employee> empList) {
		employeeMapper.batchUpdate(empList);
	}

}
