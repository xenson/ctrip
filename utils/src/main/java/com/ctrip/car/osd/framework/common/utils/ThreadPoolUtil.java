package com.ctrip.car.osd.framework.common.utils;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.ctrip.car.osd.framework.common.utils.thread.ThreadPoolConfig;
import com.dianping.cat.async.CatAsync;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自定义线程池,引入增强TheadLocal
 */
public class ThreadPoolUtil {

    /**
     * 当线程池里线程都在使用时,由调用线程进行任务调用
     *
     * @param nameFormat 线程池名 例: thread-pool-%d
     * @param poolSize   最大线程数
     * @return ExecutorService
     */
    public static ExecutorService callerRunsPool(String nameFormat, Integer poolSize) {
        return customRunPool(new ThreadPoolConfig.Builder(nameFormat, poolSize).build());
    }

    /**
     * 单个线程池
     *
     * @param nameFormat 线程池名 例: thread-pool-%d
     * @return ExecutorService
     */
    public static ExecutorService singleRunPool(String nameFormat) {
        return wrap(Executors.newSingleThreadExecutor(getThreadFactory(nameFormat)));
    }

    /**
     * 通过ThreadPoolConfig自定义线程池
     *
     * @param config maximumPoolSize默认和corePoolSize相同
     *               keepAliveTime默认60
     *               unit默认单位秒
     *               workQueue默认SynchronousQueue
     *               handler默认CallerRunsPolicy
     * @return ExecutorService
     */
    public static ExecutorService customRunPool(ThreadPoolConfig config) {
        return wrap(new ThreadPoolExecutor(config.getCorePoolSize(), config.getMaximumPoolSize(),
                config.getKeepAliveTime(), config.getUnit(), config.getWorkQueue(),
                getThreadFactory(config.getNameFormat()), config.getHandler()));
    }

    public static ThreadFactory getThreadFactory(String nameFormat) {
        ThreadFactoryBuilder threadFactoryBuilder = new ThreadFactoryBuilder();
        if (StringUtils.isNotBlank(nameFormat)) {
            threadFactoryBuilder.setNameFormat(StringUtils.trim(nameFormat));
        }
        threadFactoryBuilder.setDaemon(true);
        return threadFactoryBuilder.build();
    }

    private static ExecutorService wrap(ExecutorService executorService) {
        return wrapCat(wrapTtl(executorService));
    }

    public static ExecutorService wrapTtl(ExecutorService executorService) {
        return TtlExecutors.getTtlExecutorService(executorService);
    }

    public static ExecutorService wrapCat(ExecutorService executorService) {
        return CatAsync.wrap(executorService);
    }
}
