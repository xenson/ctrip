package com.ctrip.car.osd.framework.cache.redis;

import com.ctrip.car.osd.framework.cache.Cache;
import com.ctrip.car.osd.framework.cache.CacheType;
import com.ctrip.car.osd.framework.common.utils.GzipUtil;
import com.ctrip.car.osd.framework.common.utils.JsonUtil;
import credis.java.client.CacheProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicLong;

public class RedisCache<V> implements Cache<String, V> {

    private static final String REDIS_CACHE_KEY = "sd:";
    private CacheProvider provider;
    private final String prefix;
    private Type type;
    private Set<String> keys;
    private Boolean gzip;
    private int expiryMillis;
    private String name;
    private List<String> changeFactors;
    private Date refreshDate;
    private AtomicLong hitCount;
    private AtomicLong missCount;

    RedisCache(CacheProvider provider, Type type, String prefix, Boolean gzip, int expiryMillis, String name, String[] changeFactors, boolean withPrefix) {
        super();
        this.expiryMillis = expiryMillis;
        this.provider = provider;
        if (withPrefix) {
            this.prefix = REDIS_CACHE_KEY + prefix + ":";
        } else {
            this.prefix = "";
        }
        this.type = type;
        this.keys = new CopyOnWriteArraySet<>();
        this.gzip = gzip;
        this.name = name;
        this.changeFactors = Arrays.asList(changeFactors);
        this.refreshDate = new Date();
        this.hitCount = new AtomicLong(0);
        this.missCount = new AtomicLong(0);
    }

    @Override
    public String cacheType() {
        return CacheType.REDIS;
    }

    @Override
    public String cacheName() {
        return name;
    }

    @Override
    public V get(String key) {
        String cahceKey = buildKey(key);
        String content = this.provider.get(cahceKey);
        if (StringUtils.isBlank(content)) {
            return null;
        }
        V result = null;
        try {
            if (gzip) {
                content = GzipUtil.uncompressToString(content);
            }
            result = JsonUtil.toJson(content, getCacheObjectType());
        } catch (Exception e) {
        }
        return result;
    }

    @Override
    public Set<String> keys() {
        return this.keys;
    }

    @Override
    public boolean containsKey(String key) {
        String buildKey = buildKey(key);
        Boolean existsInRedis = this.provider.exists(buildKey);
        boolean existsInMem = this.keys.contains(buildKey);
        if (!existsInRedis && existsInMem) {
            removeKeyFromMap(buildKey);
        } else if (existsInRedis && !existsInMem) {
            addKeyToMap(buildKey);
        }
        if (existsInRedis) {
            hitCount.incrementAndGet();
        } else {
            missCount.incrementAndGet();
        }
        return existsInRedis;
    }

    @Override
    public Map<String, V> getAll(Collection<String> keys) {
        Map<String, V> values = new HashMap<>();
        for (String key : keys) {
            values.put(key, get(key));
        }
        return values;
    }

    @Override
    public Map<String, V> asMap() {
        return getAll(keys());
    }

    @Override
    public List<String> getChangeFactors() {
        return this.changeFactors;
    }

    @Override
    public boolean put(String key, V value) {
        String buildKey = buildKey(key);
        String content = getContent(value);
        boolean successed = this.provider.set(buildKey, content);
        if (successed) {
            addKeyToMap(buildKey);
            refreshDate = new Date();
            if (expiryMillis > 0) {
                int seconds = expiryMillis / 1000;
                return this.provider.expire(buildKey, seconds);
            }
            return true;
        } else {
            return false;
        }
    }

    private String getContent(V value) {
        String content = toJson(value);
        if (gzip) {
            content = GzipUtil.compressToString(content);
        }
        return content;
    }

    @Override
    public void putAll(Map<String, V> values) {
        values.forEach(this::put);
        refreshDate = new Date();
    }

    @Override
    public void clear() {
        Set<String> keys = getAllRedisKeys();
        String[] allKeys = keys.toArray(new String[0]);
        this.provider.del(allKeys);
        this.keys.clear();
        refreshDate = new Date();
    }

    private Set<String> getAllRedisKeys() {
        Set<String> allkeys = new HashSet<>();
        String newKey = buildKey("*");
        List<Integer> groupIds = this.provider.RedisGroupIds();
        if (!CollectionUtils.isEmpty(groupIds)) {
            for (Integer groupId : groupIds) {
                ScanParams params = new ScanParams();
                params.count(10000);
                params.match(newKey);
                String cursor = ScanParams.SCAN_POINTER_START;
                do {
                    ScanResult<String> scan = provider.scan(groupId, cursor, params);
                    cursor = scan.getStringCursor();
                    allkeys.addAll(scan.getResult());
                } while (!ScanParams.SCAN_POINTER_START.equals(cursor));
            }
        }
        return allkeys;
    }

    @Override
    public void clear(String key) {
        String buildKey = buildKey(key);
        this.provider.del(buildKey);
        removeKeyFromMap(buildKey);
        refreshDate = new Date();
    }

    private void addKeyToMap(String buildKey) {
        this.keys.add(buildKey);
    }

    private void removeKeyFromMap(String buildKey) {
        keys.remove(buildKey);
    }

    @Override
    public void clearAll(Collection<String> keys) {
        for (String key : keys) {
            clear(key);
        }
    }

    private String buildKey(String key) {
        if (StringUtils.isNotBlank(this.prefix) && !key.startsWith(this.prefix)) {
            key = this.prefix + key;
        }
        return key;
    }

    private String toJson(Object value) {
        return JsonUtil.toJson(value);
    }

    private Type getCacheObjectType() {
        return type;
    }

    @Override
    public Date refreshDate() {
        return refreshDate;
    }

    @Override
    public int expiryMillis() {
        return 0;
    }

    @Override
    public int maxCount() {
        return 0;
    }

    @Override
    public double hitRate() {
        long requestCount = this.hitCount() + this.missCount();
        double hitRate = (requestCount == 0) ? 1.0 : (double) hitCount() / requestCount;
        return (double) Math.round(hitRate * 100) / 100;
    }

    @Override
    public long hitCount() {
        return this.hitCount.longValue();
    }

    @Override
    public long missCount() {
        return this.missCount.longValue();
    }
}
