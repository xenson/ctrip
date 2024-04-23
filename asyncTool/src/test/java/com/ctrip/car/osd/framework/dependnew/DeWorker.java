package com.ctrip.car.osd.framework.dependnew;

import com.ctrip.car.osd.framework.async.callback.ICallback;
import com.ctrip.car.osd.framework.async.callback.IWorker;
import com.ctrip.car.osd.framework.async.worker.WorkResult;
import com.ctrip.car.osd.framework.async.wrapper.WorkerWrapper;

import java.util.Map;

public class DeWorker implements IWorker<String, User>, ICallback<String, User> {

    @Override
    public User action(String requestParam, Map<String, WorkerWrapper> allWrappers) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new User("user0");
    }


    @Override
    public User defaultValue() {
        return new User("default User");
    }

    @Override
    public void begin() {
        //System.out.println(Thread.currentThread().getName() + "- start --" + System.currentTimeMillis());
    }

    @Override
    public void result(boolean success, String requestParam, WorkResult<User> workResult) {
        System.out.println("worker0 的结果是：" + workResult.getResult());
    }

}
