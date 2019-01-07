package com.atguigu.mapper.mappers;

import com.atguigu.mapper.entities.Employee;

import tk.mybatis.mapper.common.Mapper;

/**
 * 具体操作数据库的Mapper接口，需要继承通用Mapper提供的核心接口：Mapper<Employee>
 * 泛型类型就是实体类的类型
 * @author Lenovo
 *
 */
public interface EmployeeMapper extends Mapper<Employee> {
	
}
