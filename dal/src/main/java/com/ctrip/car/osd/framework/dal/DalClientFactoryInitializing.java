package com.ctrip.car.osd.framework.dal;

import org.springframework.beans.factory.InitializingBean;

import com.ctrip.platform.dal.dao.DalClientFactory;

public class DalClientFactoryInitializing implements InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {
		DalClientFactory.initClientFactory();
		DalClientFactory.warmUpConnections();
	}

}
