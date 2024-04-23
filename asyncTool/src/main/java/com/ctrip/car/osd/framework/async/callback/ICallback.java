package com.ctrip.car.osd.framework.async.callback;

import com.ctrip.car.osd.framework.async.worker.WorkResult;

/**
 * 每个执行单元执行完毕后，会回调该接口
 * </p>
 * 需要监听执行结果的，实现该接口即可
 *
 * @author xh.gao
 */
@FunctionalInterface
public interface ICallback<T, V> {

    /**
     * 任务开始的监听
     */
    default void begin() {
    }

    /**
     * 耗时操作执行完毕后，就给value注入值
     *
     * @param success      超时失败或者action里面抛出异常，这里的success都会是false，可以依据false的case做日志记录
     * @param requestParam 原始请求的入参
     * @param workResult   单个子任务结果的包装
     */
    void result(boolean success, T requestParam, WorkResult<V> workResult);
}
