package com.ctrip.car.osd.framework.cache.consts;

public class CacheConst {
    public static final int ONE_DAY = 24*60*60*1000;
    public static final int TWENTY_MINUTES = 20*60*1000;
    public static final int ONE_HOUR = 60*60*1000;
    public static final int TEN_MINUTES = 10*60*1000;
    public static final int FIVE_MINUTES = 5*60*1000;
    public static final int ONE_MINUTES = 60*1000;
    public static final int TWO_MINUTES = 2*60*1000;

    public static final int INTERVAL_TEN_MINUTES = 10*60;
    public static final int INTERVAL_TWENTY_MINUTES = 20*60;

    public static final String Redis_DB = "carosdcommoncache";
    public static final String Redis_PREFIX = "sd:";

    public static final String REDIS_REFRESH_KEY = "redisRefreshTimeKey";
}