package com.ctrip.car.osd.framework.cache;

public enum CacheRefreshType {
    NO_REFRESH(0),
    REFRESH_WHEN_GET(1);

    private final int value;

    private CacheRefreshType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
