package com.maeil.rtm.mapper;

public abstract interface ServiceMapper {
	
	public abstract String selectDELI() throws Exception;
	public abstract String selectS1() throws Exception;
	public abstract String selectDaedan() throws Exception;
}
