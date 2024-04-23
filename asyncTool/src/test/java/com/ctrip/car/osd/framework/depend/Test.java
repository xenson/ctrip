package com.ctrip.car.osd.framework.depend;

import com.ctrip.car.osd.framework.async.executor.Async;
import com.ctrip.car.osd.framework.async.worker.WorkResult;
import com.ctrip.car.osd.framework.async.wrapper.WorkerWrapper;

import java.util.concurrent.ExecutionException;

/**
 * 后面请求依赖于前面请求的执行结果
 */
public class Test {

    public static void main(String[] args) throws Exception {
        DeWorker first = new DeWorker();
        DeWorker1 second = new DeWorker1();
        DeWorker2 third = new DeWorker2();

        WorkerWrapper<WorkResult<User>, String> thirdWorkerWrapper =  new WorkerWrapper.Builder<WorkResult<User>, String>()
                .worker(third)
                .callback(third)
                .id("third")
                .build();

        WorkerWrapper<WorkResult<User>, User> secondWorkerWrapper = new WorkerWrapper.Builder<WorkResult<User>, User>()
                .worker(second)
                .callback(second)
                .id("second")
                .next(thirdWorkerWrapper)
                .build();

        WorkerWrapper<String, User> firstWorkerWrapper = new WorkerWrapper.Builder<String, User>()
                .worker(first)
                .param("0")
                .id("first")
                .next(secondWorkerWrapper, true)
                .callback(first)
                .build();

        //虽然尚未执行，但是也可以先取得结果的引用，作为下一个任务的入参。V1.2前写法，需要手工给
        //V1.3后，不用给wrapper setParam了，直接在worker的action里自行根据id获取即可.参考dependnew包下代码
        WorkResult<User> result = firstWorkerWrapper.getWorkResult();
        WorkResult<User> result1 = secondWorkerWrapper.getWorkResult();
        secondWorkerWrapper.setParam(result);
        thirdWorkerWrapper.setParam(result1);

        Async.beginWork(3500, firstWorkerWrapper);

        System.out.println(thirdWorkerWrapper.getWorkResult());
        Async.shutDown();
    }
}
