package com.ctrip.car.osd.framework.cache.proxy;

import com.ctrip.car.osd.framework.cache.Cache;
import com.ctrip.car.osd.framework.cache.vi.CacheStats;
import com.ctrip.car.osd.framework.cache.vi.RefreshAllCacheCell;
import com.ctrip.framework.vi.cacheRefresh.CacheManager;

import java.util.*;


/**
 * Cache统计代理
 * @author xiayx@Ctrip.com
 *
 * @param <K>
 * @param <V>
 */
public class CacheStatsProxy<K, V> implements Cache<K, V> {

	private Cache<K, V> cache;
	private CacheStats<K, V> stats;

	public CacheStatsProxy(String name,String type, Cache<K, V> cache) {
		super();
		this.cache = cache;
		stats = new CacheStats<>(name,type,cache);
	}

	@Override
	public String cacheType() {
		return this.cache.cacheType();
	}

	@Override
	public String cacheName() {
		return this.cache.cacheName();
	}

	@Override
	public V get(K key) {
		return this.cache.get(key);
	}

	@Override
	public Set<K> keys() {
		return this.cache.keys();
	}

	@Override
	public boolean containsKey(K key) {
		return this.cache.containsKey(key);
	}

	@Override
	public Map<K, V> getAll(Collection<K> keys) {
		return this.cache.getAll(keys);
	}

	@Override
	public Map<K, V> asMap() {
		return this.cache.asMap();
	}

	@Override
	public List<String> getChangeFactors() {
		return this.cache.getChangeFactors();
	}

	@Override
	public boolean put(K key, V value) {
		return this.cache.put(key, value);
	}

	@Override
	public void putAll(Map<K, V> values) {
		this.cache.putAll(values);
	}

	@Override
	public void clear() {
		this.cache.clear();
	}

	@Override
	public void clear(K key) {
		this.cache.clear(key);
	}

	@Override
	public void clearAll(Collection<K> keys) {
		this.cache.clearAll(keys);
	}

	@Override
	public Date refreshDate() {
		return this.cache.refreshDate();
	}

	@Override
	public int expiryMillis() {
		return this.cache.expiryMillis();
	}

	@Override
	public int maxCount() {
		return this.cache.maxCount();
	}

	@Override
	public double hitRate() {
		return this.cache.hitRate();
	}

	@Override
	public long hitCount() {
		return this.cache.hitCount();
	}

	@Override
	public long missCount() {
		return this.cache.missCount();
	}
}
