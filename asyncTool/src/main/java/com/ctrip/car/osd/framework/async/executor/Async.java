package com.ctrip.car.osd.framework.async.executor;

import com.ctrip.car.osd.framework.async.callback.DefaultGroupCallback;
import com.ctrip.car.osd.framework.async.callback.IGroupCallback;
import com.ctrip.car.osd.framework.async.exception.TaskTimeOutException;
import com.ctrip.car.osd.framework.async.worker.ResultState;
import com.ctrip.car.osd.framework.async.wrapper.WorkerWrapper;
import com.ctrip.car.osd.framework.common.utils.ThreadPoolUtil;
import org.apache.commons.collections.CollectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 异步编排入口
 *
 * @author xh.gao
 */
@SuppressWarnings("rawtypes")
public class Async {

    /**
     * 默认线程池
     */
    private static final ExecutorService DEFAULT_COMMON_POOL = ThreadPoolUtil.callerRunsPool("asyncTool-pool-%d", 10);

    /**
     * 出发点
     */
    public static boolean beginWork(long timeout, ExecutorService executorService, List<WorkerWrapper> workerWrappers) throws Exception {
        if (workerWrappers == null || workerWrappers.size() == 0) {
            return false;
        }
        //定义一个map，存放所有的wrapper，key为wrapper的唯一id，value是该wrapper，可以从value中获取wrapper的result
        Map<String, WorkerWrapper> forParamUseWrappers = new ConcurrentHashMap<>();
        CompletableFuture[] futures = new CompletableFuture[workerWrappers.size()];
        for (int i = 0; i < workerWrappers.size(); i++) {
            WorkerWrapper wrapper = workerWrappers.get(i);
            futures[i] = CompletableFuture.runAsync(() -> wrapper.work(executorService, timeout, forParamUseWrappers), executorService);
        }
        try {
            CompletableFuture.allOf(futures).get(timeout, TimeUnit.MILLISECONDS);
            return true;
        } catch (TimeoutException e) {
            Set<WorkerWrapper> set = new HashSet<>();
            totalWorkers(workerWrappers, set);
            for (WorkerWrapper wrapper : set) {
                wrapper.stopNow();
            }
            return false;
        } finally {
            taskCheck(workerWrappers);
        }
    }

    /**
     * 如果想自定义线程池，请传executorService。不自定义的话，就走默认的{@link Async#DEFAULT_COMMON_POOL}
     */
    public static boolean beginWork(long timeout, ExecutorService executorService, WorkerWrapper... workerWrapper) throws Exception {
        if (workerWrapper == null || workerWrapper.length == 0) {
            return false;
        }
        List<WorkerWrapper> workerWrappers = Arrays.stream(workerWrapper).collect(Collectors.toList());
        beginWork(timeout, executorService, workerWrappers);
        return true;
    }

    private static void taskCheck(List<WorkerWrapper> workerWrappers) throws Exception {
        Set<WorkerWrapper> allTasks = new HashSet<>();
        totalWorkers(workerWrappers, allTasks);
        Map<ResultState, List<WorkerWrapper>> allTaskStateMap = allTasks.stream().collect(
                Collectors.groupingBy(x -> x.getWorkResult().getResultState(), Collectors.mapping(Function.identity(), Collectors.toList())));
        if (CollectionUtils.isNotEmpty(allTaskStateMap.get(ResultState.EXCEPTION))) {
            for (WorkerWrapper result : allTaskStateMap.get(ResultState.EXCEPTION)) {
                ResultState resultState = result.getWorkResult().getResultState();
                if (ResultState.EXCEPTION.equals(resultState)) {
                    throw result.getWorkResult().getEx();
                }
            }
        }
        if (CollectionUtils.isNotEmpty(allTaskStateMap.get(ResultState.TIMEOUT))) {
            for (WorkerWrapper result : allTaskStateMap.get(ResultState.TIMEOUT)) {
                ResultState resultState = result.getWorkResult().getResultState();
                if (ResultState.TIMEOUT.equals(resultState)) {
                    throw new TaskTimeOutException(result.getId());
                }
            }
        }
    }

    /**
     * 同步阻塞,直到所有都完成,或失败
     */
    public static boolean beginWork(long timeout, WorkerWrapper... workerWrapper) throws Exception {
        return beginWork(timeout, DEFAULT_COMMON_POOL, workerWrapper);
    }

    public static void beginWorkAsync(long timeout, IGroupCallback groupCallback, WorkerWrapper... workerWrapper) {
        beginWorkAsync(timeout, DEFAULT_COMMON_POOL, groupCallback, workerWrapper);
    }

    /**
     * 异步执行,直到所有都完成,或失败后，发起回调
     */
    public static void beginWorkAsync(long timeout, ExecutorService executorService, IGroupCallback groupCallback, WorkerWrapper... workerWrapper) {
        if (groupCallback == null) {
            groupCallback = new DefaultGroupCallback();
        }
        IGroupCallback finalGroupCallback = groupCallback;
        if (executorService != null) {
            executorService.submit(() -> {
                try {
                    boolean success = beginWork(timeout, executorService, workerWrapper);
                    if (success) {
                        finalGroupCallback.success(Arrays.asList(workerWrapper));
                    } else {
                        finalGroupCallback.failure(Arrays.asList(workerWrapper), new TimeoutException());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    finalGroupCallback.failure(Arrays.asList(workerWrapper), e);
                }
            });
        } else {
            DEFAULT_COMMON_POOL.submit(() -> {
                try {
                    boolean success = beginWork(timeout, DEFAULT_COMMON_POOL, workerWrapper);
                    if (success) {
                        finalGroupCallback.success(Arrays.asList(workerWrapper));
                    } else {
                        finalGroupCallback.failure(Arrays.asList(workerWrapper), new TimeoutException());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    finalGroupCallback.failure(Arrays.asList(workerWrapper), e);
                }
            });
        }

    }

    /**
     * 总共多少个执行单元
     */
    @SuppressWarnings("unchecked")
    public static void totalWorkers(List<WorkerWrapper> workerWrappers, Set<WorkerWrapper> set) {
        set.addAll(workerWrappers);
        for (WorkerWrapper wrapper : workerWrappers) {
            if (wrapper.getNextWrappers() == null) {
                continue;
            }
            List<WorkerWrapper> wrappers = wrapper.getNextWrappers();
            totalWorkers(wrappers, set);
        }
    }

    /**
     * 关闭线程池
     */
    public static void shutDown() {
        shutDown(null);
    }

    /**
     * 关闭线程池
     */
    public static void shutDown(ExecutorService executorService) {
        if (executorService != null) {
            executorService.shutdown();
        } else {
            DEFAULT_COMMON_POOL.shutdown();
        }
    }

    public static String getThreadCount() {
        ThreadPoolExecutor commonPool = null;
        try {
            Field h = DEFAULT_COMMON_POOL.getClass().getSuperclass().getDeclaredField("executor");
            h.setAccessible(true);
            Object executorServiceTtlWrapper = h.get(DEFAULT_COMMON_POOL);
            Field executorService = executorServiceTtlWrapper.getClass().getDeclaredField("executorService");
            executorService.setAccessible(true);
            commonPool = (ThreadPoolExecutor) executorService.get(executorServiceTtlWrapper);
        } catch (Exception e) {
            System.err.println("DEFAULT_COMMON_POOL config error");
        }

        return commonPool == null ? null : "activeCount=" + commonPool.getActiveCount() +
                "  completedCount " + commonPool.getCompletedTaskCount() +
                "  largestCount " + commonPool.getLargestPoolSize();
    }
}
