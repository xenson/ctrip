package com.ctrip.car.osd.framework.cache;

public enum CacheValueType {
    CACHE_NOT_EMPTY(0),CACHE_EMPTY(1);

    private final int value;

    private CacheValueType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
