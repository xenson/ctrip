package com.ctrip.car.osd.framework.soa.server.autoconfigure;

import com.ctrip.car.osd.framework.common.config.SDFrameworkQConfig;
import com.ctrip.car.osd.framework.common.properties.CtripConfigProperties;
import com.ctrip.car.osd.framework.soa.server.ExecutorFactory;
import com.ctrip.car.osd.framework.soa.server.InterceptorFactory;
import com.ctrip.car.osd.framework.soa.server.ServiceExecutorEngine;
import com.ctrip.car.osd.framework.soa.server.interceptors.CommonBizExceptionInterceptor;
import com.ctrip.car.osd.framework.soa.server.interceptors.MobileRequestInterceptor;
import com.ctrip.car.osd.framework.soa.server.serviceinterceptor.ServiceInterceptorSupport;
import com.ctrip.car.osd.framework.soa.server.validator.ValidationInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SoaServiceConfigProperties.class)
@EnableSoa2ServiceProxy
public class ServiceExecutorConfiguration {

	@Bean
	public SDFrameworkQConfig sdFrameworkQConfig() {
		return new SDFrameworkQConfig();
	}

	@Bean
	public SoaConfigService soaConfigService() {
		return new SoaConfigService();
	}

	@Bean
	public ServiceExecutorEngine executorEngine() {
		return new ServiceExecutorEngine();
	}

	@Bean
	public ExecutorFactory executorFactory(CtripConfigProperties ctripConfigProperties) {
		return new ExecutorFactory(ctripConfigProperties);
	}

	@Bean
	public InterceptorFactory interceptorFactory() {
		return new InterceptorFactory();
	}

	@Bean
	public ValidationInterceptor validationInterceptor() {
		return new ValidationInterceptor();
	}

	@Bean
	public ServiceInterceptorSupport serviceInterceptorSupported(SDFrameworkQConfig qconfig){
		return new ServiceInterceptorSupport(qconfig);
	}
	
	@Bean
	public MobileRequestInterceptor mobileRequestInterceptor(){
		return new MobileRequestInterceptor();
	}

	@Bean
	public CommonBizExceptionInterceptor commonBizExceptionInterceptor() {
		return new CommonBizExceptionInterceptor();
	}


}
