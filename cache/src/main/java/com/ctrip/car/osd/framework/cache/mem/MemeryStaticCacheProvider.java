package com.ctrip.car.osd.framework.cache.mem;

import java.lang.reflect.Type;

import com.ctrip.car.osd.framework.cache.Cache;
import com.ctrip.car.osd.framework.cache.CacheProvider;
import com.ctrip.car.osd.framework.cache.CacheType;
import com.ctrip.car.osd.framework.cache.Cacheable;


public class MemeryStaticCacheProvider implements CacheProvider {
	
	@Override
	public String getName() {
		return CacheType.MEM;
	}
	
	@Override
	public <K, V> Cache<K, V> build(String cacheName, Type fieldType, Cacheable cacheable2) {
		return new MemeryStaticCache<>(cacheable2.name(),cacheable2.expiryMillis(), cacheable2.maxCount(),cacheable2.changeFactors());
	}

}
