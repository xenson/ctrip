package com.ctrip.car.osd.framework.soa.client.autoconfigure;

import java.util.Set;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;

import com.ctrip.car.osd.framework.common.scan.ScanClassAutoConfigureRegister;
import com.ctrip.car.osd.framework.common.scan.Scanner;
import com.ctrip.car.osd.framework.soa.client.ServiceClient;
import com.ctrip.car.osd.framework.soa.client.ServiceClientFacadeFactoryBean;
import com.ctrip.car.osd.framework.soa.client.ServiceClientFactoryBean;
import com.ctrip.soa.framework.soa.v1.NonSLBRegistryServiceClient;
import com.ctriposs.baiji.rpc.client.ServiceClientBase;

public class ServiceClientAutoConfigureRegistrar extends ScanClassAutoConfigureRegister {

	public ServiceClientAutoConfigureRegistrar() {
		super();
	}
	
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		Scanner scanner = new Scanner(getBasePackage());
		
		// register Service Client
		Set<Class<?>> classes = scanner.scan(ServiceClientBase.class);
		classes.remove(NonSLBRegistryServiceClient.class);
		for (Class clazz : classes) {
			if( !clazz.getName().startsWith("com.ctrip.soa.platform") ) {
				GenericBeanDefinition beanDefinition = createServiceClientBeanDefinition(clazz);
				registry.registerBeanDefinition(clazz.getName(), beanDefinition);
			}
		}
		
		//register Service Client Facade 
		Set<Class<?>> facadeClasses = scanner.scanWithAnnotated(ServiceClient.class);
		for (Class clazz : facadeClasses) {
			GenericBeanDefinition beanDefinition = createServiceClientFacadeBeanDefinition(clazz);
			registry.registerBeanDefinition(clazz.getName(), beanDefinition);
		}
	}
	
	
	@Override
	protected Set<Class<?>> doScan(Scanner scanners) {
		return scanners.scan(ServiceClientBase.class);
	}

	private GenericBeanDefinition createServiceClientBeanDefinition(Class<?> beanClass) {
		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(ServiceClientFactoryBean.class);
		beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(beanClass.getName());
		beanDefinition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_NO);
		return beanDefinition;
	}
	
	private GenericBeanDefinition createServiceClientFacadeBeanDefinition(Class<?> beanClass) {
		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(ServiceClientFacadeFactoryBean.class);
		beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(beanClass.getName());
		beanDefinition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_NO);
		return beanDefinition;
	}

}
