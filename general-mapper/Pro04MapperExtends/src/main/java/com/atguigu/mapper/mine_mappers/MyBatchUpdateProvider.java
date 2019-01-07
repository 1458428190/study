package com.atguigu.mapper.mine_mappers;

import java.util.Set;

import org.apache.ibatis.mapping.MappedStatement;

import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;

public class MyBatchUpdateProvider extends MapperTemplate {

	public MyBatchUpdateProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
		super(mapperClass, mapperHelper);
	}
	
	/*
		<foreach collection="list" item="record" separator=";" >
			UPDATE tabple_emp
			<set>
				emp_name=#{record.empName},
				emp_age=#{record.empAge},
				emp_salary=#{record.empSalary},
			</set>
			where emp_id=#{record.empId}
		</foreach>
	 */
	public String batchUpdate(MappedStatement statement) {
		
		//1.创建StringBuilder用于拼接SQL语句的各个组成部分
		StringBuilder builder = new StringBuilder();
		
		//2.拼接foreach标签
		builder.append("<foreach collection=\"list\" item=\"record\" separator=\";\" >");
		
		//3.获取实体类对应的Class对象
		Class<?> entityClass = super.getEntityClass(statement);
		
		//4.获取实体类在数据库中对应的表名
		String tableName = super.tableName(entityClass);
		
		//5.生成update子句
		String updateClause = SqlHelper.updateTable(entityClass, tableName);
		
		builder.append(updateClause);
		
		builder.append("<set>");
		
		//6.获取所有字段信息
		Set<EntityColumn> columns = EntityHelper.getColumns(entityClass);
		
		String idColumn = null;
		String idHolder = null;
		
		for (EntityColumn entityColumn : columns) {
			
			boolean isPrimaryKey = entityColumn.isId();
			
			//7.判断当前字段是否为主键
			if(isPrimaryKey) {
				
				//8.缓存主键的字段名和字段值
				idColumn = entityColumn.getColumn();
				
				//※返回格式如:#{record.age,jdbcType=NUMERIC,typeHandler=MyTypeHandler}
				idHolder = entityColumn.getColumnHolder("record");
				
			}else {
				
				//9.使用非主键字段拼接SET子句
				String column = entityColumn.getColumn();
				String columnHolder = entityColumn.getColumnHolder("record");
				
				builder.append(column).append("=").append(columnHolder).append(",");
				
			}
			
		}
		
		builder.append("</set>");
		
		//10.使用前面缓存的主键名、主键值拼接where子句
		builder.append("where ").append(idColumn).append("=").append(idHolder);
		
		builder.append("</foreach>");
		
		//11.将拼接好的字符串返回
		return builder.toString();
	}

}
