package com.ctrip.car.osd.framework.cache.manage;

import com.ctrip.car.osd.framework.cache.Cache;
import com.ctrip.car.osd.framework.cache.CacheFactory;
import com.ctrip.car.osd.framework.cache.CacheType;
import com.ctrip.car.osd.framework.common.utils.JsonUtil;
import com.ctrip.framework.vi.cms.CMSHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by zmxie on 2019/8/16.
 */
@RestController
public class CacheController {

    @Autowired
    private CacheFactory cacheFactory;

    private static final String LOCAL_CACHE_URL = "http://%s:8080/cache/local/%s";
    private static final String EMPTY_RESULT = "empty";
    private static final String SUCCESS = "success";
    private static final String FAILURE = "failure";

    /**
     * 获取内存缓存
     */
    @GetMapping(value = {"/local/{cacheName}/{key}", "/local/{cacheName}"})
    public String getLocalCache(@PathVariable("cacheName") String cacheName,
                                @PathVariable(value = "key", required = false) String key) {

        if (!cacheFactory.contains(cacheName)) {
            return EMPTY_RESULT;
        }
        Map<?, ?> cache = cacheFactory.getCache(cacheName).asMap();
        return JsonUtil.toJson(StringUtils.isNotEmpty(key) ? cache.get(key) : cache);
    }

    @GetMapping(value = {"/{appid}/{cacheName}/{key}", "/{appid}/{cacheName}"})
    public String getAppCache(@PathVariable("appid") String appid,
                              @PathVariable("cacheName") String cacheName,
                              @PathVariable(value = "key", required = false) String key) {

        if (!cacheFactory.contains(cacheName)) {
            return EMPTY_RESULT;
        }
        boolean containsKey = StringUtils.isNotEmpty(key);
        Cache cache = cacheFactory.getCache(cacheName);
        if (isMemory(cache.cacheType())) {
            Map<String, String> cacheMap = new HashMap<>();
            for (String ip : getAppIpList(appid)) {
                String url = generateUrl(LOCAL_CACHE_URL, ip, cacheName, key);
                RestTemplate restTemplate = new RestTemplate();
                try {
                    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                    if (response.getStatusCode() != HttpStatus.OK) {
                        continue;
                    }
                    String localCache = response.getBody();
                    if (StringUtils.isEmpty(localCache)) {
                        continue;
                    }
                    cacheMap.put(ip, localCache);
                } catch (Exception e) {
                    cacheMap.put(ip, EMPTY_RESULT);
                }
            }
            return JsonUtil.toJson(cacheMap);
        } else {
            Map<?, ?> redisCacheMap = cache.asMap();
            return JsonUtil.toJson(containsKey ? redisCacheMap.get(key) : redisCacheMap);
        }
    }

    /**
     * 清除指定内存缓存
     */
    @SuppressWarnings("unchecked")
    @DeleteMapping(value = {"/local/{cacheName}/{key}", "/local/{cacheName}"})
    public String refreshLocalCache(@PathVariable("cacheName") String cacheName,
                                    @PathVariable(value = "key", required = false) String key) {

        if (!cacheFactory.contains(cacheName)) {
            return FAILURE;
        }
        Cache cache = cacheFactory.getCache(cacheName);
        if (StringUtils.isNotEmpty(key)) {
            cache.clear(key);
        } else {
            cache.clear();
        }
        return SUCCESS;
    }

    @SuppressWarnings("unchecked")
    @DeleteMapping(value = {"/{appid}/{cacheName}/{key}", "/{appid}/{cacheName}"})
    public String refreshAppCache(@PathVariable("appid") String appid,
                                  @PathVariable("cacheName") String cacheName,
                                  @PathVariable(value = "key", required = false) String key) {

        if (!cacheFactory.contains(cacheName)) {
            return FAILURE;
        }
        boolean containsKey = StringUtils.isNotEmpty(key);
        Cache cache = cacheFactory.getCache(cacheName);
        if (isMemory(cache.cacheType())) {
            Map<String, String> refreshLocalCacheResult = new HashMap<>();
            for (String ip : getAppIpList(appid)) {
                String url = generateUrl(LOCAL_CACHE_URL, ip, cacheName, key);
                RestTemplate restTemplate = new RestTemplate();
                try {
                    restTemplate.delete(url);
                    refreshLocalCacheResult.put(ip, SUCCESS);
                } catch (Exception e) {
                    refreshLocalCacheResult.put(ip, FAILURE);
                }
            }
            return JsonUtil.toJson(refreshLocalCacheResult);
        } else {
            if (containsKey) {
                cache.clear(key);
            } else {
                cache.clear();
            }
            Map<String, String> refreshRedisResult = new HashMap<>();
            refreshRedisResult.put(appid, SUCCESS);
            return JsonUtil.toJson(refreshRedisResult);
        }
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/local")
    public String updateLocalCache(@RequestBody LocalCacheRequest request) {
        String cacheName = request.getCacheName();
        String key = request.getKey();
        String value = request.getValue();
        if (StringUtils.isAnyEmpty(cacheName, key, value) || !cacheFactory.contains(cacheName)) {
            return FAILURE;
        }
        Cache localCache = cacheFactory.getCache(cacheName);
        localCache.put(key, value);
        return SUCCESS;
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/{appid}")
    public String updateLocalCache(@PathVariable("appid") String appid, @RequestBody LocalCacheRequest request) {
        String cacheName = request.getCacheName();
        String key = request.getKey();
        String value = request.getValue();
        if (StringUtils.isAnyEmpty(cacheName, key, value) || !cacheFactory.contains(cacheName)) {
            return FAILURE;
        }
        Cache cache = cacheFactory.getCache(cacheName);
        if (isMemory(cache.cacheType())) {
            Map<String, String> updateLocalCacheResult = new HashMap<>();
            for (String ip : getAppIpList(appid)) {
                String url = String.format("http://%s:8080/cache/local", ip);
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
                if (response.getStatusCode().equals(HttpStatus.OK)) {
                    updateLocalCacheResult.put(ip, response.getBody());
                } else {
                    updateLocalCacheResult.put(ip, FAILURE);
                }
            }
            return JsonUtil.toJson(updateLocalCacheResult);
        } else {
            cache.put(key, value);
            Map<String, String> refreshRedisResult = new HashMap<>();
            refreshRedisResult.put(appid, SUCCESS);
            return JsonUtil.toJson(refreshRedisResult);
        }
    }

    private boolean isMemory(String cacheType) {
        return cacheType.equals(CacheType.MEM);
    }

    private List<String> getAppIpList(String appid) {
        //String appid = Foundation.app().getAppId();
        List<CMSHelper.CmsServerDetail> serverList = CMSHelper.getServersByAppId(appid);
        return serverList.stream().map(server -> server.ip).collect(Collectors.toList());
    }

    private String generateUrl(String template, String ip, String cacheName, String key) {
        String url = String.format(template, ip, cacheName);
        if (StringUtils.isNotEmpty(key)) {
            url = url + "/" + key;
        }
        return url;
    }
}
