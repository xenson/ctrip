package com.ctrip.car.osd.framework.dal.transaction;

import java.lang.reflect.Method;

import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.beans.BeansException;
import org.springframework.core.annotation.AnnotationUtils;

public class TransactionProxyFactoryBean extends AbstractAutoProxyCreator {

	private static final long serialVersionUID = 1L;

	@Override
	protected Object[] getAdvicesAndAdvisorsForBean(Class<?> beanClass, String beanName,
			TargetSource customTargetSource) throws BeansException {
		
		Transactional transactional = AnnotationUtils.findAnnotation(beanClass, Transactional.class);
		if( transactional != null) {
			return PROXY_WITHOUT_ADDITIONAL_INTERCEPTORS;
		}
		
		Method[] methods = beanClass.getDeclaredMethods();
		for( Method method : methods) {
			transactional = AnnotationUtils.findAnnotation(method, Transactional.class);
			if( transactional != null) {
				return PROXY_WITHOUT_ADDITIONAL_INTERCEPTORS;
			}
		}
		return DO_NOT_PROXY;
	}

}
