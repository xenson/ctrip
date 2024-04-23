package com.ctrip.car.osd.framework.async.callback;

import com.ctrip.car.osd.framework.async.wrapper.WorkerWrapper;

import java.util.Map;

/**
 * 每个最小执行单元需要实现该接口
 *
 * @author xh.gao
 */
@FunctionalInterface
public interface IWorker<T, V> {

    /**
     * 在这里做耗时操作，如soa请求、IO等
     *
     * @param requestParam 入参
     * @param allWrappers  所有任务的包装持有者，可以通过id获取对应的任务
     * @return 该子任务的返回对象
     */
    V action(T requestParam, Map<String, WorkerWrapper> allWrappers);

    /**
     * 超时、异常时，返回的默认值
     *
     * @return 默认值
     */
    default V defaultValue() {
        return null;
    }
}
