package com.ctrip.car.osd.framework.soa.server.serviceinterceptor;

public interface ServiceInterceptor {
	
	void before(ServiceContext context);
	
	void after(ServiceContext context);

	void exception(ServiceContext context,Exception exception);
}
