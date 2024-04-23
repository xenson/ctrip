package com.ctrip.car.osd.framework.soa.server;

public interface ExceptionInterceptor {
	
	void exceptionHandle(Object request , Object response , Exception exception) throws Exception;

	default boolean canHandle(Exception exception) {
		return false;
	}
}
