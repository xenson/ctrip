package com.ctrip.car.osd.framework.cache.proxy;

import com.ctrip.car.osd.framework.cache.*;
import com.ctrip.car.osd.framework.common.clogging.Logger;
import com.ctrip.car.osd.framework.common.clogging.LoggerFactory;
import com.ctrip.car.osd.framework.common.context.RequestContext;
import com.ctrip.car.osd.framework.common.context.RequestContextFactory;
import com.ctrip.car.osd.framework.common.exception.BizException;
import com.ctrip.car.osd.framework.common.helper.RedisClusterNameHelper;
import com.ctrip.car.osd.framework.common.utils.StringUtil;
import credis.java.client.CacheProvider;
import org.apache.commons.collections.MapUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CacheAspectProxy {
	private final Logger logger = LoggerFactory.getLogger("CacheAspectProxy");
	private Map<String, IKeyGenerator> generatorMap = new ConcurrentHashMap<>();
	private static final CacheProvider redisProvider = credis.java.client.util.CacheFactory.GetProvider(RedisClusterNameHelper.getClusterName("carosdcommoncache"));

	@Autowired
	private CacheFactory cacheFactory;

	@Around("@annotation(cacheable2)")
	public Object cacheProxy(ProceedingJoinPoint point, Cacheable cacheable2) throws Throwable {
        try {
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            Class<?>[] parameterTypes = method.getParameterTypes();
            Type methodReturnType = method.getGenericReturnType();
			Cache cache2 = getCache(point, cacheable2, methodReturnType);
            Object[] args = point.getArgs();
            String cacheKey = getKey(cacheable2, parameterTypes, args);

            boolean ignoreCache = false;
			RequestContext requestContext = RequestContextFactory.INSTANCE.getCurrent();
			if (requestContext != null && requestContext.getIgnoreCache() != null) {
				ignoreCache = requestContext.getIgnoreCache();
			}
			if (StringUtil.isEmpty(cacheKey) || ignoreCache) {
				return point.proceed();
			}
			if (cache2.containsKey(cacheKey)) {
                Object result = cache2.get(cacheKey);
                if (result == null) {
                    return getResult(point, cacheable2, cache2,cacheKey);
                }
                return result;
            }
            return getResult(point, cacheable2, cache2, cacheKey);
        } catch (Exception e) {
            return point.proceed();
        }
	}

    private Object getResult(ProceedingJoinPoint point, Cacheable cacheable2, Cache cache2, String cacheKey) {
		Serializable lockObj = LockUtil.getLockObj(cacheKey, cache2);
		synchronized (lockObj) {
			String cacheType = cache2.cacheType();
			String lockKey = cacheKey + "_lock";
			try {
				if (lockAndCheckCache(cache2, cacheKey, cacheType, lockKey, cacheable2.waitLockCount())){
					return cache2.get(cacheKey);
				}
				Object result = point.proceed();
				putCache(cacheable2, cache2, cacheKey, result);
				return result;
			} catch (BizException e) {
				throw e;
			} catch (Throwable e) {
				return null;
			} finally {
				delRedisLockKey(cacheType, lockKey);
			}
		}
    }

	private void delRedisLockKey(String cacheType, String lockKey) {
		if (CacheType.REDIS.equalsIgnoreCase(cacheType)) {
            redisProvider.del(lockKey);
        }
	}

	private void putCache(Cacheable cacheable2, Cache cache2, String cacheKey, Object result) {
		if (result != null) {
			CacheValueType cacheValueType = cacheable2.cacheValueType();
			if (cacheValueType == CacheValueType.CACHE_EMPTY || (cacheValueType == CacheValueType.CACHE_NOT_EMPTY && notEmpty(result))) {
				cache2.put(cacheKey, result);
			}
		}
	}

	private boolean lockAndCheckCache(Cache cache2, String cacheKey, String cacheType, String lockKey, int waitLockCount) throws InterruptedException {
		if (CacheType.REDIS.equalsIgnoreCase(cacheType)) {
			if (!tryLock(lockKey)) {
				logger.info("NoRedisLock",lockKey);
                int index = 0;
				int waitCount = waitLockCount <=0 ? 20 : waitLockCount;
                while (index < waitCount) {
                    Thread.sleep(100);
                    //等待锁的过程中命中了缓存，直接读取，如果没有命中尝试重新获取锁
					if (cache2.containsKey(cacheKey)) {
						return true;
					} else if (tryLock(lockKey)) {
						return false;
					}
					index++;
                }
				logger.info("TimeoutRedisLock",lockKey);
            }
        }
		return cache2.containsKey(cacheKey);
	}

	private Boolean tryLock(String lockKey) {
		return redisProvider.set(lockKey, "1", "NX", "EX", 20);
	}

	private boolean notEmpty(Object result) {
		if (result instanceof List) {
			return !CollectionUtils.isEmpty((List) result);
		} else if (result instanceof Map) {
			return !MapUtils.isEmpty((Map) result);
		} else {
			return result != null;
		}
	}

	private String getKey(Cacheable anno, Class<?>[] parameterTypes,
						  Object[] arguments) throws InstantiationException,
			IllegalAccessException {
		String key;
		String generatorClsName = anno.generator().getName();
		IKeyGenerator keyGenerator;
		if (anno.generator().equals(DefaultKeyGenerator.class)) {
			keyGenerator = new DefaultKeyGenerator();
		} else {
			if (generatorMap.containsKey(generatorClsName)) {
				keyGenerator = generatorMap.get(generatorClsName);
			} else {
				keyGenerator = anno.generator().newInstance();
				generatorMap.put(generatorClsName, keyGenerator);
			}
		}

		key = keyGenerator.getKey(anno.key(), parameterTypes,arguments);
		return key;
	}

	private Cache getCache(ProceedingJoinPoint point, Cacheable cacheable2,Type returnType) {
		String key = getCacheName(point, cacheable2);
		if (cacheFactory.contains(key)) {
			return cacheFactory.getCache(key);
		} else {
			return cacheFactory.createCache(cacheable2.type(), key, returnType, cacheable2);
		}
	}

	private String getCacheName(ProceedingJoinPoint point, Cacheable cacheable2) {
		String name = cacheable2.name();
		if (name.isEmpty()) {
			return point.getSignature().getDeclaringTypeName() + "." + point.getSignature().getName();
		}
		return name;
	}

}
