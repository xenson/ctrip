package com.ctrip.car.osd.framework.dependnew;

import com.ctrip.car.osd.framework.async.executor.Async;
import com.ctrip.car.osd.framework.async.wrapper.WorkerWrapper;

import java.util.concurrent.ExecutionException;

/**
 * 后面请求依赖于前面请求的执行结果
 */
public class Test {

    public static void main(String[] args) throws Exception {
        DeWorker w = new DeWorker();
        DeWorker1 w1 = new DeWorker1();
        DeWorker2 w2 = new DeWorker2();

        WorkerWrapper<User, String> workerWrapper2 =  new WorkerWrapper.Builder<User, String>()
                .worker(w2)
                .callback(w2)
                .id("third")
                .build();

        WorkerWrapper<String, User> workerWrapper1 = new WorkerWrapper.Builder<String, User>()
                .worker(w1)
                .callback(w1)
                .id("second")
                .next(workerWrapper2)
                .build();

        WorkerWrapper<String, User> workerWrapper = new WorkerWrapper.Builder<String, User>()
                .worker(w)
                .param("0")
                .id("first")
                .next(workerWrapper1)
                .callback(w)
                .build();

        //V1.3后，不用给wrapper setParam了，直接在worker的action里自行根据id获取即可

        Async.beginWork(3500, workerWrapper);

        System.out.println(workerWrapper2.getWorkResult());
        Async.shutDown();
    }
}
