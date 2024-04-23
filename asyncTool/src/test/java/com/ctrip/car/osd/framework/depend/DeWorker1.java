package com.ctrip.car.osd.framework.depend;

import com.ctrip.car.osd.framework.async.callback.ICallback;
import com.ctrip.car.osd.framework.async.callback.IWorker;
import com.ctrip.car.osd.framework.async.worker.WorkResult;
import com.ctrip.car.osd.framework.async.wrapper.WorkerWrapper;

import java.util.Map;

public class DeWorker1 implements IWorker<WorkResult<User>, User>, ICallback<WorkResult<User>, User> {

    @Override
    public User action(WorkResult<User> requestParam, Map<String, WorkerWrapper> allWrappers) {
        System.out.println("par1的入参来自于par0： " + requestParam.getResult());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new User("user1");
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
    public void result(boolean success, WorkResult<User> requestParam, WorkResult<User> workResult) {
        System.out.println("worker1 的结果是：" + workResult.getResult());
    }

}
