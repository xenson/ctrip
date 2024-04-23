package com.ctrip.car.osd.framework.dal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.ctrip.car.osd.framework.common.utils.TypeUtils;
import com.ctrip.car.osd.framework.dal.query.DalQuery;
import com.ctrip.platform.dal.dao.DalHints;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Mixin;

public class DalRepositoryFactoryBean<T> implements FactoryBean<T> {
	
	private Class<T> repository;
	@Autowired
	private DalQuery dalQuery;


	public DalRepositoryFactoryBean(Class<T> repository) {
		super();
		this.repository = repository;
	}

	@Override
	public T getObject() throws Exception {

		Class<?> entityType = getEntityType();
		Mixin mixin = Mixin.create(new Class[] { DalRepository.class, repository },
				new Object[] { new DalRepositoryImpl(entityType), createRepositoryInstance() });
		return (T) mixin;
	}

	private Object createRepositoryInstance() {
		Enhancer enhancer = new Enhancer();
		enhancer.setInterfaces(new Class[] { this.repository });
		enhancer.setCallback(new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				List<Object> parameters = new ArrayList<>();
				DalHints hints = null;
				for (Object arg : args) {
					if (arg instanceof DalHints) {
						hints = (DalHints) arg;
					}
					parameters.add(arg);
				}
				return getDalQuery().query(method, parameters, hints);
			}
		});
		return enhancer.create();
	}

	private DalQuery getDalQuery() {
		return this.dalQuery;
	}

	@Override
	public Class<?> getObjectType() {
		return this.repository;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	private Class<?> getEntityType() {
		return TypeUtils.getGenericType(repository, DalRepository.class);
	}

}
