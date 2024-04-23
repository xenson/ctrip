package com.ctrip.car.osd.framework.cache.vi;

import com.ctrip.car.osd.framework.cache.Cache;
import com.ctrip.framework.vi.cacheRefresh.CacheCell;

import java.util.HashMap;
import java.util.Map;

public class RefreshAllCacheCell<K, V> implements CacheCell {

	private Map<String, Object> status;
	private Cache<K, V> cache2;
	private String name;
	private String type;

	public RefreshAllCacheCell(String name, String type, Cache<K, V> cache2) {
		super();
		this.name = name;
		this.type = type;
		this.cache2 = cache2;
		this.status = new HashMap<>();
	}

	@Override
	public Map<String, Object> getStatus() {
		this.status.put("type", type);
		this.status.put("refresh time", cache2.refreshDate());
		this.status.put("hit rate", cache2.hitRate());
		this.status.put("hit count", cache2.hitCount());
		this.status.put("miss count", cache2.missCount());
		return this.status;
	}

	@Override
	public Object getByKey(String s) {
		return this.cache2.get((K)s);
	}

	@Override
	public String id() {
		return this.name ;
	}

	@Override
	public boolean refresh() {
		this.cache2.clear();
		this.status.put("refresh time", cache2.refreshDate());
		return true;
	}

	@Override
	public int size() {
		return this.cache2.keys().size();
	}


	@Override
	public Iterable keys() {
		return this.cache2.keys();
	}

}
