package com.ctrip.car.osd.framework.common.entity.co;

public class RedisClusterNameMapConfigCO {

    private String dataCenter;
    private String targetName;

    public void setDataCenter(String dataCenter) {
        this.dataCenter = dataCenter;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getDataCenter() {
        return dataCenter;
    }

    public String getTargetName() {
        return targetName;
    }

}
