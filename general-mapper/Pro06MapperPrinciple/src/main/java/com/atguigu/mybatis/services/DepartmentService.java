package com.atguigu.mybatis.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atguigu.mybatis.entities.Department;
import com.atguigu.mybatis.mappers.DepartmentMapper;

@Service
public class DepartmentService {
	
	@Autowired
	private DepartmentMapper departmentMapper;
	
	public List<Department> getAll() {
		return departmentMapper.selectAll();
	}

}
