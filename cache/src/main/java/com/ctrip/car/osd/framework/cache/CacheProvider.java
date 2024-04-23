package com.ctrip.car.osd.framework.cache;

import java.lang.reflect.Type;

public interface CacheProvider {

	String getName();

	<K, V> Cache<K, V> build(String cacheName , Type fieldType , Cacheable cacheable2);

}
