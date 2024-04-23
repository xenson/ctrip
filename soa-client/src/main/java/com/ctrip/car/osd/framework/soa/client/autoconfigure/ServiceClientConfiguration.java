package com.ctrip.car.osd.framework.soa.client.autoconfigure;

import com.ctrip.car.osd.framework.common.config.SDFrameworkQConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.ctrip.car.osd.framework.soa.client.invoker.ClientInvokerProxy;

@Configuration
@Import(ServiceClientAutoConfigureRegistrar.class)
public class ServiceClientConfiguration {
	
	@Bean
	public ClientInvokerProxy clientInvokerProxy(){
		return new ClientInvokerProxy();
	}

	@Bean
	public ServiceClientProperties serviceClientProperties() {
		return new ServiceClientProperties();
	}

	@Bean
	public SDFrameworkQConfig sdFrameworkQConfig() {
		return new SDFrameworkQConfig();
	}
}
