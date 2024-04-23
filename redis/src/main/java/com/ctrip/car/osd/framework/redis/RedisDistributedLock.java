package com.ctrip.car.osd.framework.redis;

import com.ctrip.car.osd.framework.common.helper.RedisClusterNameHelper;
import com.ctrip.car.osd.framework.redis.exception.LockedException;
import com.ctrip.car.osd.framework.redis.exception.UnlockedException;
import com.ctrip.framework.clogging.agent.log.ILog;
import com.ctrip.framework.clogging.agent.log.LogManager;
import credis.java.client.CacheProvider;
import credis.java.client.util.CacheFactory;

import java.io.Serializable;

/**
 * @author by xiayx on 2019/7/29 9:47
 */
public class RedisDistributedLock implements Lock, Serializable {
    private static final long serialVersionUID = 18321093218900325L;
    private static final CacheProvider redisProvider = CacheFactory.GetProvider(RedisClusterNameHelper.getClusterName("carosdcommoncache"));
    private static final int DEFAULT_INTERVAL_SECONDS = 1;
    private static final int TIME_OUT_MULTIPLE = 60;
    private static final int MAX_LOCK_TIMES = 60;
    private static final int MAX_UNLOCK_TIMES = 60;
    private static ILog logger = LogManager.getLogger(RedisDistributedLock.class);
    private String key;
    private volatile Boolean status = false;
    private Integer interval;
    private Integer maxLockTimes;
    private Integer maxUnLockTimes;

    public RedisDistributedLock(String key) {
        this.key = key;
        this.interval = DEFAULT_INTERVAL_SECONDS;
        this.maxLockTimes = MAX_LOCK_TIMES;
        this.maxUnLockTimes = MAX_UNLOCK_TIMES;
    }

    public RedisDistributedLock(String key, Integer interval) {
        this.key = key;
        this.interval = interval;
    }

    public RedisDistributedLock(String key, Integer interval, Integer maxLockTimes, Integer maxUnLockTimes) {
        this.key = key;
        this.interval = interval;
        this.maxLockTimes = maxLockTimes;
        this.maxUnLockTimes = maxUnLockTimes;
    }

    public String getLockKey() {
        return this.key;
    }

    public Boolean getLockStatus() {
        return this.status;
    }

    public synchronized Boolean lock() throws LockedException {
        if (this.status) {
            throw new LockedException(String.format("lock(key:%s) locked", this.key));
        } else {
            this.status = true;
            int lockTimes = 0;
            while(lockTimes < this.maxLockTimes && !redisProvider.set(this.key,String.valueOf(this.status),"NX","EX",this.interval * TIME_OUT_MULTIPLE)) {
                ++lockTimes;
                try {
                    this.wait(this.interval * 1000L);
                } catch (InterruptedException var3) {
                    logger.error(String.format("lock(key:%s, interval:%s)", this.key, this.interval), var3);
                    Thread.currentThread().interrupt();
                }
            }

            if (lockTimes >= this.maxLockTimes) {
                throw new LockedException(String.format("lock(key:%s, interval:%s, times:%s) maximum times(%s) tried but failed", this.key, this.interval, lockTimes, this.maxUnLockTimes));
            } else {
                return this.status;
            }
        }
    }

    public synchronized Boolean tryLock(){
        return redisProvider.set(this.key,String.valueOf(this.status),"NX","EX",this.interval * TIME_OUT_MULTIPLE);
    }

    public synchronized Boolean unlock() throws UnlockedException {
        if (this.status && redisProvider.exists(this.key)) {
            int unlockTimes = 0;

            while(unlockTimes < this.maxUnLockTimes && !redisProvider.del(this.key)) {
                ++unlockTimes;
                try {
                    this.wait(this.interval * 1000L);
                } catch (InterruptedException var3) {
                    logger.error(String.format("unlock(key:%s, interval:%s, times:%s)", this.key, this.interval, unlockTimes), var3);
                    Thread.currentThread().interrupt();
                }
            }

            if (unlockTimes >= this.maxUnLockTimes) {
                logger.warn("unlock", String.format("unlock(key:%s, interval:%s, times:%s) maximum times(%s) tried but failed", this.key, this.interval, unlockTimes, this.maxUnLockTimes));
            }

            this.status = false;
            return this.status;
        } else {
            throw new UnlockedException(String.format("unlock(key:%s) released", this.key));
        }
    }
}
