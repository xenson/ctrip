package com.ctrip.car.osd.framework.lock;

import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author xh.gao
 * @createTime 2022年08月01日 18:00:00
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributeLock {

    /**
     * 分布式锁的前缀：推荐自定义传一个。如果不传默认使用方法所在类名称作为锁的前缀
     */
    String preFix() default StringUtils.EMPTY;

    /**
     * spEl表达式解析的key。如果不传默认使用uid作为锁的最小粒度
     */
    String lockLey() default StringUtils.EMPTY;

    /**
     * 是否需要阻塞获取锁。默认不阻塞
     */
    boolean needBlock() default false;

    /**
     * 阻塞获取锁的时长。默认为5s
     */
    long waitTime() default 5L;

    /**
     * 阻塞获取锁等待时长的单位。默认为秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
