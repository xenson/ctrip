package com.ctrip.car.osd.framework.cache;

import java.util.*;

/**
 * 
 * 缓存系统接口 , 接近底层处理
 * 
 * @author xiayx@ctrip.com
 *
 * @param <K>
 * @param <V>
 * @see CacheFactory
 */
public interface Cache<K, V> {
	String cacheType();

	String cacheName();

	V get(K key);

	Set<K> keys();
	
	boolean containsKey(K key);

	Map<K, V> getAll(Collection<K> keys);

	Map<K, V> asMap();

	List<String> getChangeFactors();

	boolean put(K key, V value);

	void putAll(Map<K, V> values);

	void clear();
	
	void clear(K key);
	
	void clearAll(Collection<K> keys);

	Date refreshDate();

	int expiryMillis();

	int maxCount();

	double hitRate();
	long hitCount();
	long missCount();
}
