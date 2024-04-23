package com.ctrip.credis;

import org.springframework.stereotype.Component;

import com.ctrip.car.osd.framework.redis.RedisProvider;

import credis.java.client.CacheProvider;


@Component
public class CacheProviderWrapper {

	@RedisProvider("carosdcommoncache")
	public CacheProvider provider;

	public CacheProvider getProvider() {
		return provider;
	}

}
