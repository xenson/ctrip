package com.ctrip.car.osd.framework.dal.mapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ctrip.car.osd.framework.dal.DalRowMapperContext;
import com.ctrip.platform.dal.dao.DalRowMapper;

public class ObjectRowMapperContext implements DalRowMapperContext {

	private Map<Class, DalRowMapper> mappers;

	public ObjectRowMapperContext() {
		this.mappers = new ConcurrentHashMap<>();
	}

	@Override
	public <T> DalRowMapper<T> getRowMapper(Class<T> entityClass) {
		
		if(mappers.containsKey(entityClass)) {
			return this.mappers.get(entityClass);
		} else {
			DalRowMapper<T> rowMapper = new ObjectRowMapper<>(entityClass);
			this.mappers.put(entityClass, rowMapper);
			return rowMapper;
		}
	}
}
