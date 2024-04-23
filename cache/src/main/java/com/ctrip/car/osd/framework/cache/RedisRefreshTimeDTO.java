package com.ctrip.car.osd.framework.cache;

/**
 * Created by chen_lh on 2019/4/19.
 */
public class RedisRefreshTimeDTO {
    private String k;//缓存key
    private long lt;//最后更新时间

    public RedisRefreshTimeDTO(String k, long lt){
        this.k = k;
        this.lt = lt;
    }

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public long getLt() {
        return lt;
    }

    public void setLt(long lt) {
        this.lt = lt;
    }
}
