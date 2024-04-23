package com.ctrip.car.osd.framework.dal.autoconfigure;

import java.util.Set;

import org.springframework.beans.factory.support.GenericBeanDefinition;

import com.ctrip.car.osd.framework.common.scan.ScanClassAutoConfigureRegister;
import com.ctrip.car.osd.framework.common.scan.Scanner;
import com.ctrip.car.osd.framework.dal.DalRepository;
import com.ctrip.car.osd.framework.dal.DalRepositoryFactoryBean;
import com.ctrip.car.osd.framework.dal.DalRepositoryImpl;

public class DalRepositoryAutoConfigureRegister extends ScanClassAutoConfigureRegister {

	@Override
	protected Set<Class<?>> doScan(Scanner scanner) {
		Set<Class<?>> repositoryClassess = scanner.scan(DalRepository.class);
		repositoryClassess.remove(DalRepositoryImpl.class);
		return repositoryClassess;
	}

	@Override
	protected GenericBeanDefinition createBeanDefinition(Class<?> beanClass) {
		GenericBeanDefinition beanDefinition = super.createBeanDefinition(DalRepositoryFactoryBean.class);
		beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(beanClass.getName());
		return beanDefinition;
	}

}
