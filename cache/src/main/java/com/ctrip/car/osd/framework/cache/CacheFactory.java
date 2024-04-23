package com.ctrip.car.osd.framework.cache;

import com.ctrip.car.osd.framework.cache.consts.CacheConst;
import com.ctrip.car.osd.framework.cache.proxy.CacheStatsProxy;
import com.ctrip.car.osd.framework.common.clogging.Logger;
import com.ctrip.car.osd.framework.common.clogging.LoggerFactory;
import com.ctrip.car.osd.framework.common.helper.RedisClusterNameHelper;
import com.ctrip.car.osd.framework.common.properties.CtripConfigProperties;
import com.ctrip.car.osd.framework.common.spring.SpringInitializingBean;
import com.ctrip.car.osd.framework.common.utils.ConfigUtils;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import java.util.OptionalLong;
import java.util.Set;
import java.util.concurrent.*;

@SuppressWarnings({"rawtypes", "unchecked" })
public class CacheFactory extends SpringInitializingBean {
	private Map<String, CacheProvider> providers;
	private Map<String, Cache> caches;
	private CtripConfigProperties configProperties;
	private credis.java.client.CacheProvider credisProvider;
	private final Logger logger;

	public CacheFactory() {
		this.logger = LoggerFactory.getLogger("CacheFactory");
		this.providers = new ConcurrentHashMap<>();
		this.caches = new ConcurrentHashMap<>();
		this.credisProvider = credis.java.client.util.CacheFactory.GetProvider(RedisClusterNameHelper.getClusterName(CacheConst.Redis_DB));
	}

	public CacheFactory(CtripConfigProperties configProperties) {
		this();
		this.configProperties = configProperties;
	}

	/**
	 * 创建缓存
	 * @param cacheType
	 * @param name
	 * @param fieldType
	 * @param cacheable2
	 * @return
	 */
	public synchronized <K, V> Cache<K, V> createCache(String cacheType, String name, Type fieldType, Cacheable cacheable2) {
		if (contains(name)) {
			return getCache(name);
		}
		if (this.providers.containsKey(cacheType)) {
			CacheProvider provider = this.providers.get(cacheType);
			Cache<K, V> cache = provider.build(name, fieldType, cacheable2);
			CacheStatsProxy<K, V> proxy = new CacheStatsProxy<>(name,cacheType, cache);
			this.caches.put(name, proxy);
			return proxy;
		}
		throw new IllegalArgumentException("Not Found The Cache Provider [" + cacheType + "]");
	}

	public synchronized void removeCache(String name) {
		if (this.caches.containsKey(name)) {
			this.caches.remove(name);
		}
	}

	public boolean contains(String cacheName) {
		return this.caches.containsKey(cacheName);
	}

	public <K, V> Cache<K, V> getCache(String cacheName) {
		return this.caches.get(cacheName);
	}

	public Map<String, Cache> getCaches() {
		return caches;
	}

	public void refresh(String cacheName) {
		if (this.caches.containsKey(cacheName)) {
			Cache cache = this.caches.get(cacheName);
			cache.clear();
		}
	}

	public void refresh(String cacheName, Object key) {
		if (this.caches.containsKey(cacheName)) {
			Cache cache = this.caches.get(cacheName);
			cache.clear(key);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.registerProviders();
		this.injectCaches();
		this.refreshDataSchedule();
		this.startRefreshCacheKeySchedule();
	}

	private void registerProviders() {
		Map<String, CacheProvider> cacheProviders = this.applicationContext.getBeansOfType(CacheProvider.class);
		for (Entry<String, CacheProvider> provider : cacheProviders.entrySet()) {
			this.providers.put(provider.getValue().getName(), provider.getValue());
		}
	}
	

	private void injectCaches() throws IllegalAccessException {
		String basePackage = ConfigUtils.getBasePackage(this.configProperties);
		Reflections scanner = new Reflections(basePackage, new FieldAnnotationsScanner());
		Set<Field> fields = scanner.getFieldsAnnotatedWith(Cacheable.class);
		for (Field field : fields) {
			Map<String, ?> values = this.applicationContext.getBeansOfType(field.getDeclaringClass());
			if (values == null || values.isEmpty()) {
				continue;
			}
			Object target = values.values().iterator().next();
			Cacheable cacheable = field.getAnnotation(Cacheable.class);
			ReflectionUtils.makeAccessible(field);
			field.set(target,
					this.createCache(cacheable.type(), cacheable.name(), field.getGenericType(), cacheable));
		}
	}

	private void refreshDataSchedule() {
		ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("RefreshCacheData-Pool-%d").setDaemon(true).build();
		ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1,threadFactory);
		scheduledThreadPool.scheduleWithFixedDelay(() -> {
			try {
			Map<String, String> redisRefreshTime = credisProvider.hgetAll(CacheConst.REDIS_REFRESH_KEY);
			if (MapUtils.isNotEmpty(redisRefreshTime) && MapUtils.isNotEmpty(caches)) {
				for (Cache cache : caches.values()) {
					if (CollectionUtils.isNotEmpty(cache.getChangeFactors()) && cache.getChangeFactors().stream().anyMatch(redisRefreshTime::containsKey)) {
						OptionalLong maxOptional = cache.getChangeFactors()
								.stream()
								.filter(redisRefreshTime::containsKey)
								.mapToLong(cf -> Long.parseLong(redisRefreshTime.get(cf)))
								.max();
						if (maxOptional.isPresent() && cache.refreshDate().getTime() < maxOptional.getAsLong()) {
							cache.clear();
						}
					}
				}
			}
			}catch(Exception e) {
				logger.warn("refreshDataSchedule_exception", e);
			}
		}, 1, 1, TimeUnit.MINUTES);
	}

	private void startRefreshCacheKeySchedule() {
		ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("RefreshCacheKey-Pool-%d").setDaemon(true).build();
		ScheduledExecutorService excurtor = Executors.newScheduledThreadPool(1,threadFactory);
		excurtor.scheduleWithFixedDelay(() -> {
					try {
						caches.forEach((name, cache) -> {
							if (CacheType.REDIS.equalsIgnoreCase(cache.cacheType())) {
								cache.keys().forEach(cache::containsKey);//判断是否存在时会自动移除无效key
							}
						});
					} catch (Exception e) {
						logger.warn("startRefreshCacheKeySchedule_exception", e);
					}
				}
		, 3, 5, TimeUnit.MINUTES);  //启动延迟3分钟执行，每隔5分钟执行一次
	}
}
