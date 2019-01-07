package com.atguigu.mapper.mappers;

import org.apache.ibatis.annotations.CacheNamespace;

import com.atguigu.mapper.entities.Employee;
import com.atguigu.mapper.mine_mappers.MyMapper;

@CacheNamespace
public interface EmployeeMapper extends MyMapper<Employee> {

}