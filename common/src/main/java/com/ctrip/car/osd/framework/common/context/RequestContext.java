package com.ctrip.car.osd.framework.common.context;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author by xiayx on 2019/5/10 10:22
 */
public class RequestContext {
    private String language;
    private String locale;
    private String currencyCode;
    private String sourceFrom;
    private String uid;
    private String requesetId;
    private String sessionId;
    private Integer sourceCountryId;
    private Integer invokeFrom;
    private Integer channelId;
    private Boolean ignoreCache;
    private Map<String,String> requestItems = new ConcurrentHashMap<>();
    private Set<ThreadLocal<Map<String, String>>> customizeTagHolder = new CopyOnWriteArraySet<>();

    public Map<String, String> getRequestItems() {
        return requestItems;
    }

    public void put(String key, String value) {
        if (key != null && value != null) {
            requestItems.put(key, value);
        }
    }

    public void putAll(Map<String,String> items){
        items.forEach(this::put);
    }

    public void get(String key) {
        requestItems.get(key);
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getSourceFrom() {
        return sourceFrom;
    }

    public void setSourceFrom(String sourceFrom) {
        this.sourceFrom = sourceFrom;
    }

    public Integer getSourceCountryId() {
        return sourceCountryId;
    }

    public void setSourceCountryId(Integer sourceCountryId) {
        this.sourceCountryId = sourceCountryId;
    }

    public Integer getInvokeFrom() {
        return invokeFrom;
    }

    public void setInvokeFrom(Integer invokeFrom) {
        this.invokeFrom = invokeFrom;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRequesetId() {
        return requesetId;
    }

    public void setRequesetId(String requesetId) {
        this.requesetId = requesetId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Boolean getIgnoreCache() {
        return ignoreCache;
    }

    public void setIgnoreCache(Boolean ignoreCache) {
        this.ignoreCache = ignoreCache;
    }

    public Set<ThreadLocal<Map<String, String>>> getCustomizeTagHolder() {
        return customizeTagHolder;
    }

    public void addToCustomizeTagHolder(TransmittableThreadLocal<Map<String, String>> customizeTag) {
        this.customizeTagHolder.add(customizeTag);
    }
}
