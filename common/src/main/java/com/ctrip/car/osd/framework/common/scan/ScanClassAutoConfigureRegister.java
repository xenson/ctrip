package com.ctrip.car.osd.framework.common.scan;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Set;

public abstract class ScanClassAutoConfigureRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware {

	/**
	 * bean扫描的包路径范围，由ctrip.base-package配置指定
	 */
	private String basePackage;

	/**
	 * soa service的全限定类名
	 */
	private String soaTargetService;

	protected Environment environment;

	public ScanClassAutoConfigureRegister() {
	}

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		Scanner scanner = new Scanner(getBasePackage());
		Set<Class<?>> classes = doScan(scanner);
		for (Class clazz : classes) {
			GenericBeanDefinition beanDefinition = createBeanDefinition(clazz);
			registry.registerBeanDefinition(clazz.getName(), beanDefinition);
		}
	}

	@Override
	public void setEnvironment(Environment environment) {
		if (this.environment != null) {
			return;
		}
		this.environment = environment;
		this.basePackage = this.environment.getProperty("ctrip.base-package", "com.ctrip");
		this.soaTargetService = this.environment.getProperty("ctrip.soaTargetService", StringUtils.EMPTY);
	}

	protected String getBasePackage() {
		return this.basePackage;
	}

	protected String getSoaTargetService() {
		return this.soaTargetService;
	}

	protected abstract Set<Class<?>> doScan(Scanner scanner);

	protected GenericBeanDefinition createBeanDefinition(Class<?> beanClass) {
		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(beanClass);
		beanDefinition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_NO);
		return beanDefinition;
	}

}
