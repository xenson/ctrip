package com.ctrip.car.osd.framework.cache.redis;

import com.ctrip.car.osd.framework.cache.Cache;
import com.ctrip.car.osd.framework.cache.CacheProvider;
import com.ctrip.car.osd.framework.cache.CacheType;
import com.ctrip.car.osd.framework.cache.Cacheable;
import com.ctrip.car.osd.framework.common.helper.RedisClusterNameHelper;
import com.ctrip.framework.foundation.Foundation;
import credis.java.client.util.CacheFactory;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Type;
import java.util.Arrays;

public class RedisCacheProvider implements CacheProvider {

	@Override
	public String getName() {
		return CacheType.REDIS;
	}

	@Override
	public <K, V> Cache<K, V> build(String cacheName, Type fieldType, Cacheable cacheable2) {
		String[] options = cacheable2.options();
		if (options.length == 0) {
			throw new IllegalArgumentException("redis cache setting error. the @InjectCache options in RedisDbName");
		}
		String redisDbName = options[0];
		if(StringUtils.isBlank(redisDbName)) {
			throw new IllegalArgumentException("redis cache setting error. the Redis DB cacheName is required.");
		}
		credis.java.client.CacheProvider provider = CacheFactory.GetProvider(RedisClusterNameHelper.getClusterName(redisDbName));
		if (provider == null) {
			throw new IllegalArgumentException("the redis (" + redisDbName  + ") not exsit error.");
		}

        boolean gzip = Arrays.asList(options).contains("gzip");

        Cache<String, V> cache = new RedisCache<>(provider, fieldType, cacheName, gzip,cacheable2.expiryMillis(),cacheable2.name(),cacheable2.changeFactors(),cacheable2.withPrefix());
		return (Cache<K, V>) cache;
	}

}