package com.ctrip.car.osd.framework.soa.server.proxy;

import java.lang.reflect.Method;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.ctrip.car.osd.framework.soa.server.ServiceExecutorEngine;
import com.ctriposs.baiji.rpc.common.types.CheckHealthResponseType;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.InvocationHandler;

public class SoaServiceFactoryBean<T> implements FactoryBean<T> {

	private Class<T> service;
	private T serviceInstance;
	@Autowired
	private ServiceExecutorEngine executorEngine;

	public SoaServiceFactoryBean(Class<T> service) {
		super();
		this.service = service;
	}

	private Object execute(Object req, Object proxy, Method method, Object[] args) {
		return executorEngine.execute(req, proxy, method, args);
	}

	private CheckHealthResponseType checkHealth(Object request) throws Exception {
		return new CheckHealthResponseType();
	}

	private void createServiceObject() {
		Enhancer enhancer = new Enhancer();
		enhancer.setInterfaces(new Class[] { this.service });
		enhancer.setCallback(new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				if (method.getName().equalsIgnoreCase("checkHealth")) {
					return checkHealth(args[0]);
				} else {
					return execute(args[0], proxy, method, args);
				}
			}
		});
		this.serviceInstance = (T) enhancer.create();
	}

	@Override
	public T getObject() throws Exception {
		if (serviceInstance == null) {
			this.createServiceObject();
		}
		return serviceInstance;
	}

	@Override
	public Class<?> getObjectType() {

		if (serviceInstance == null) {
			return this.service;
		} else {
			return this.serviceInstance.getClass();
		}
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	public Class<T> getService() {
		return service;
	}

	public void setService(Class<T> service) {
		this.service = service;
	}

	public ServiceExecutorEngine getExecutorEngine() {
		return executorEngine;
	}

	public void setExecutorEngine(ServiceExecutorEngine executorEngine) {
		this.executorEngine = executorEngine;
	}

}
