package com.ctrip.car.osd.framework.soa.server.serviceinterceptor;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * 获取服务的上下文信息
 * 
 * @author xiayx@Ctrip.com
 *
 */
public class ServiceContextUtils {

	private static ThreadLocal<ServiceContext> contextMap = new TransmittableThreadLocal<>();

	public static ServiceContext getContext() {
		return contextMap.get();
	}
	
	public static void setContext(ServiceContext context) {
		contextMap.set(context);
	}

	public static void release() {
		contextMap.remove();
	}
}
