package com.ctrip.car.osd.framework.dal.autoconfigure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.ctrip.car.osd.framework.dal.transaction.TransactionMethodInterceptor;
import com.ctrip.car.osd.framework.dal.transaction.TransactionProxyFactoryBean;

@EnableAspectJAutoProxy(proxyTargetClass = true)
public class DalTransactionConfiguration {

//	@Bean
//	public TransactionInterceptor transactionInterceptor() {
//		return new TransactionInterceptor();
//	}
	
	@Bean("dalTransactionMethodInterceptor")
	public TransactionMethodInterceptor dalTransactionMethodInterceptor(){
		return new TransactionMethodInterceptor();
	}
	
	@Bean
	public TransactionProxyFactoryBean transactionProxyFactoryBean(){
		TransactionProxyFactoryBean proxyFactoryBean = new TransactionProxyFactoryBean();
		proxyFactoryBean.setInterceptorNames("dalTransactionMethodInterceptor");
		return proxyFactoryBean;
	}
}
