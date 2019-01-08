package com.atguigu.mp.beans;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.baomidou.mybatisplus.enums.IdType;

//@KeySequence(value="seq_user",clazz=Integer.class)
public class User extends Parent {
	//@TableId(type=IdType.INPUT)
	private Integer id  ;
	
	@TableField(fill=FieldFill.INSERT_UPDATE)
	private String name ;
	
	@TableLogic   // 逻辑删除属性
	private Integer logicFlag ;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getLogicFlag() {
		return logicFlag;
	}
	public void setLogicFlag(Integer logicFlag) {
		this.logicFlag = logicFlag;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", logicFlag=" + logicFlag + "]";
	}
	
}
