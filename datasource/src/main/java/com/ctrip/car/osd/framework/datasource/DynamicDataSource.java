package com.ctrip.car.osd.framework.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.ctrip.car.osd.framework.common.clogging.Logger;
import com.ctrip.car.osd.framework.common.clogging.LoggerFactory;

public class DynamicDataSource extends AbstractRoutingDataSource {
	
	private static final Logger logger = LoggerFactory.getLogger(DynamicDataSource.class);
	
	@Override
	protected Object determineCurrentLookupKey() {
		logger.info("switch dataSource key " + CustomerContextHolder.getCustomerType());
		return CustomerContextHolder.getCustomerType();
		
	}

}
