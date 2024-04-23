package com.ctrip.car.osd.framework.lock.configuration;

import com.ctrip.arch.distlock.DistributedLockService;
import com.ctrip.arch.distlock.redis.RedisDistributedLockService;
import com.ctrip.car.osd.framework.lock.DistributeLockAspect;
import com.ctrip.car.osd.framework.lock.helper.RedisClusterNameHelper;
import com.ctrip.car.osd.framework.lock.UidResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xh.gao
 * @createTime 2022年08月01日 18:05:00
 */
@Configuration
public class DistributedLockConfiguration {

    @Bean
    public DistributedLockService distributedLockService() {
        return new RedisDistributedLockService(RedisClusterNameHelper.getClusterName());
    }

    @Bean
    public UidResolver uidService() {
        return new UidResolver();
    }

    @Bean
    public DistributeLockAspect distributeLockAspect() {
        return new DistributeLockAspect();
    }
}

