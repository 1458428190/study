package com.atguigu.mapper.entities;

public enum SeasonEnum {
	
	SPRING("spring @_@"),SUMMER("summer @_@"),AUTUMN("autumn @_@"),WINTER("winter @_@");
	
	private String seasonName;
	
	private SeasonEnum(String seasonName) {
		this.seasonName = seasonName;
	}
	
	public String getSeasonName() {
		return this.seasonName;
	}
	
	public String toString() {
		return this.seasonName;
	}

}
