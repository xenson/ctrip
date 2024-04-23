package com.ctrip.car.osd.framework.soa.server.autoconfigure;

import com.ctrip.car.osd.framework.common.scan.ScanClassAutoConfigureRegister;
import com.ctrip.car.osd.framework.common.scan.Scanner;
import com.ctrip.car.osd.framework.soa.server.exception.ServiceNotFoundException;
import com.ctrip.car.osd.framework.soa.server.proxy.SoaServiceFactoryBean;
import com.ctriposs.baiji.rpc.common.BaijiContract;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import java.util.Objects;
import java.util.Set;

public class SoaServiceAutoConfigureRegistrar extends ScanClassAutoConfigureRegister {

	@Override
	protected Set<Class<?>> doScan(Scanner scanner) {
		Set<Class<?>> classes = Sets.newCopyOnWriteArraySet(scanner.scanWithAnnotated(BaijiContract.class));
		preFilter(classes);
		SoaServiceRegistry.registerSoaService(classes);
		return classes;
	}

	private void preFilter(Set<Class<?>> classes) {
		//1. 基础过滤
		classes.removeIf(clazz -> !clazz.isInterface());

		//2.为兼容历史已有的配置"ctrip.basePackage"，针对包名进行过滤
		String soaPackage = this.environment.getProperty("ctrip.basePackage");
		if (StringUtils.isNotEmpty(soaPackage)) {
			classes.removeIf(clazz -> !clazz.getPackage().getName().startsWith(soaPackage));
			if (classes.isEmpty()) {
				throw new ServiceNotFoundException("Please check the configuration of the file [application.properties] with properties [ctrip.basePackage]");
			}
		}

		//3.如果应用主动指定了soa接口的全限定类名，则进行对应匹配
		String soaTargetService = getSoaTargetService();
		if (StringUtils.isNotEmpty(soaTargetService)) {
			classes.removeIf(clazz -> !Objects.equals(clazz.getName(), soaTargetService));
			if (classes.isEmpty()) {
				throw new ServiceNotFoundException("Please check the configuration of the file [application.properties] with properties [ctrip.soaTargetService]");
			}
		}
	}

	@Override
	protected GenericBeanDefinition createBeanDefinition(Class<?> beanClass) {
		GenericBeanDefinition beanDefinition = super.createBeanDefinition(SoaServiceFactoryBean.class);
		beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(beanClass.getName());
		return beanDefinition;
	}
}
