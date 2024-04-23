package com.ctrip.car.osd.framework.lock.entity;

import lombok.Data;

/**
 * @author xh.gao
 * @date 2023/6/27 22:50
 */
@Data
public class RedisClusterNameMapConfigCO {
    private String dataCenter;
    private String targetName;
}
