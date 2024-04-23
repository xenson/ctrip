package com.ctrip.car.osd.framework.soa.client;

import org.apache.commons.lang.reflect.MethodUtils;
import org.springframework.beans.factory.FactoryBean;

public class ServiceClientFactoryBean<T> implements FactoryBean<T> {

	private Class<T> serviceClientInterface;

	public ServiceClientFactoryBean(Class<T> serviceClientInterface) {
		super();
		this.serviceClientInterface = serviceClientInterface;
	}

	@Override
	public T getObject() throws Exception {
		return (T) MethodUtils.invokeStaticMethod(serviceClientInterface, "getInstance", new Object[0]);
	}

	@Override
	public Class<?> getObjectType() {
		return serviceClientInterface;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
