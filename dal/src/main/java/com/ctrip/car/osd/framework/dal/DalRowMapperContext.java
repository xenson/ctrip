package com.ctrip.car.osd.framework.dal;

import com.ctrip.platform.dal.dao.DalRowMapper;

public interface DalRowMapperContext {
	
	<T> DalRowMapper<T> getRowMapper( Class<T> entityClass );

}
