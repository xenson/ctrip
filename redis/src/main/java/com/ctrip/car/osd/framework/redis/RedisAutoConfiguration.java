package com.ctrip.car.osd.framework.redis;

import org.springframework.context.annotation.Bean;


public class RedisAutoConfiguration {

	@Bean
	public RedisInitializing redisInitializing() {
		return new RedisInitializing();
	}

}
