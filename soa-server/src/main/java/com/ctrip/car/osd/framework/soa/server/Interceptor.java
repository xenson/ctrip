package com.ctrip.car.osd.framework.soa.server;

public interface Interceptor {

	void handle(Object value) throws Exception;
	
}
