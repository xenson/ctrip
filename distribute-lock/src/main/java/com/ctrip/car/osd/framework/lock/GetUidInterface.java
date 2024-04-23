package com.ctrip.car.osd.framework.lock;

/**
 * @author xh.gao
 * @createTime 2022年08月01日 18:10:00
 */
public interface GetUidInterface {

    /**
     * 获取uid的方法，由各应用自行实现
     *
     * @return uid
     * @throws Exception
     */
    String getUid() throws Exception;
}
