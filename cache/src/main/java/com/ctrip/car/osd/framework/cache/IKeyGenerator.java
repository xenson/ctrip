package com.ctrip.car.osd.framework.cache;

public abstract class IKeyGenerator {
    public static final String LINK = "_";

    public String getKey(String key,Class<?>[] parameterTypes, Object[] arguments) {
        StringBuffer sb = new StringBuffer("");
        key = buildKey(key,parameterTypes, arguments);
        sb.append(key);
        return sb.toString();
    }

    public abstract String buildKey(String key, Class<?>[] parameterTypes, Object[] arguments);
}
