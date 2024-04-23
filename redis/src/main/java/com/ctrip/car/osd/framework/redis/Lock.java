package com.ctrip.car.osd.framework.redis;

import com.ctrip.car.osd.framework.redis.exception.LockedException;
import com.ctrip.car.osd.framework.redis.exception.UnlockedException;

/**
 * @author by xiayx on 2019/7/29 9:53
 */
public interface Lock {
    Boolean lock() throws LockedException;

    Boolean unlock() throws UnlockedException;

    String getLockKey();

    Boolean getLockStatus();
}
