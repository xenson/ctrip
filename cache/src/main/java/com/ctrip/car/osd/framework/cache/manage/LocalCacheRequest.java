package com.ctrip.car.osd.framework.cache.manage;

/**
 * Created by zmxie on 2019/8/21.
 */
public class LocalCacheRequest {

    private String cacheName;
    private String key;
    private String value;

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
