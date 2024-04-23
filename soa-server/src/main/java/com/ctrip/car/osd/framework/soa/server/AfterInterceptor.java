package com.ctrip.car.osd.framework.soa.server;

public interface AfterInterceptor {

	void afterHandle(Object request, Object response) throws Exception;
}
