package com.ctrip.car.osd.framework.soa.server.serviceinterceptor;

/**
 * 全局的 service filter
 * 
 * @author xiayx@ctrip.com
 * 
 */
public interface GlobalServiceInterceptor extends ServiceInterceptor {
	
	String name();
	
}
