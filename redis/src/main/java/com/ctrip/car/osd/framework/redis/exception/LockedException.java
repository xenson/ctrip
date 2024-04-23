package com.ctrip.car.osd.framework.redis.exception;

/**
 * @author by xiayx on 2019/7/29 9:50
 */
public class LockedException extends Exception {
    public LockedException(String message) {
        super(message);
    }
}
