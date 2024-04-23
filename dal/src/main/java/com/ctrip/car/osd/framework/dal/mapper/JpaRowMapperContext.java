package com.ctrip.car.osd.framework.dal.mapper;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ctrip.car.osd.framework.dal.DalRowMapperContext;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;

public class JpaRowMapperContext implements DalRowMapperContext {

	private Map<Class, DalRowMapper> mappers;

	public JpaRowMapperContext() {
		this.mappers = new ConcurrentHashMap<>();
	}

	@Override
	public <T> DalRowMapper<T> getRowMapper(Class<T> entityClass) {
		if (mappers.containsKey(entityClass)) {
			return this.mappers.get(entityClass);
		} else {
			try {
				DalRowMapper<T> rowMapper = new DalDefaultJpaParser<>(entityClass);
				this.mappers.put(entityClass, rowMapper);
				return rowMapper;
			} catch (SQLException e) {
				throw new IllegalArgumentException("create jpa row mapper error.", e);
			}
		}
	}

}
