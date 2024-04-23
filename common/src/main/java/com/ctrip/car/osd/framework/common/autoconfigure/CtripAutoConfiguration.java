package com.ctrip.car.osd.framework.common.autoconfigure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ctrip.car.osd.framework.common.config.ConfigStartup;
import com.ctrip.car.osd.framework.common.properties.CtripConfigProperties;

@Configuration
@EnableConfigurationProperties({ CtripConfigProperties.class})
public class CtripAutoConfiguration {

	@Bean
	public ConfigStartup configStartup() {
		return new ConfigStartup();
	}

}
