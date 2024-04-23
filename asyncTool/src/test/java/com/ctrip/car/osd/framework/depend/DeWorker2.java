package com.ctrip.car.osd.framework.depend;

import com.ctrip.car.osd.framework.async.callback.ICallback;
import com.ctrip.car.osd.framework.async.callback.IWorker;
import com.ctrip.car.osd.framework.async.worker.WorkResult;
import com.ctrip.car.osd.framework.async.wrapper.WorkerWrapper;

import java.util.Map;

public class DeWorker2 implements IWorker<WorkResult<User>, String>, ICallback<WorkResult<User>, String> {

    @Override
    public String action(WorkResult<User> requestParam, Map<String, WorkerWrapper> allWrappers) {
        System.out.println("par2的入参来自于par1： " + requestParam.getResult());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return requestParam.getResult().getName();
    }


    @Override
    public String defaultValue() {
        return "default";
    }

    @Override
    public void begin() {
        //System.out.println(Thread.currentThread().getName() + "- start --" + System.currentTimeMillis());
    }

    @Override
    public void result(boolean success, WorkResult<User> requestParam, WorkResult<String> workResult) {
        System.out.println("worker2 的结果是：" + workResult.getResult());
    }

}
