package com.ctrip.car.osd.framework.common.utils.thread;

import java.util.concurrent.*;

public class ThreadPoolConfig {
    private final String nameFormat;
    private final Integer corePoolSize;
    private final Integer maximumPoolSize;
    private final Long keepAliveTime;
    private final TimeUnit unit;
    private final BlockingQueue<Runnable> workQueue;
    private final RejectedExecutionHandler handler;

    private ThreadPoolConfig(Builder builder) {
        this.nameFormat = builder.nameFormat;
        this.corePoolSize = builder.corePoolSize;
        this.maximumPoolSize = builder.maximumPoolSize;
        this.keepAliveTime = builder.keepAliveTime;
        this.unit = builder.unit;
        this.workQueue = builder.workQueue;
        this.handler = builder.handler;
    }

    public static class Builder {
        private final String nameFormat;
        private final Integer corePoolSize;
        private Integer maximumPoolSize;
        private Long keepAliveTime = 60L;
        private TimeUnit unit = TimeUnit.SECONDS;
        private BlockingQueue<Runnable> workQueue = new SynchronousQueue<>();
        private RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();

        public Builder(String nameFormat, int corePoolSize) {
            this.nameFormat = nameFormat;
            this.corePoolSize = corePoolSize;
        }

        public Builder maximumPoolSize(int maximumPoolSize) {
            this.maximumPoolSize = maximumPoolSize;
            return this;
        }

        public Builder keepAliveTime(long keepAliveTime) {
            this.keepAliveTime = keepAliveTime;
            return this;
        }

        public Builder unit(TimeUnit unit) {
            this.unit = unit;
            return this;
        }

        public Builder workQueue(BlockingQueue<Runnable> workQueue) {
            this.workQueue = workQueue;
            return this;
        }

        public Builder handler(RejectedExecutionHandler handler) {
            this.handler = handler;
            return this;
        }


        public ThreadPoolConfig build() {
            if (this.maximumPoolSize == null) {
                this.maximumPoolSize = this.corePoolSize;
            }
            return new ThreadPoolConfig(this);
        }
    }

    public String getNameFormat() {
        return nameFormat;
    }

    public Integer getCorePoolSize() {
        return corePoolSize;
    }

    public Integer getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public Long getKeepAliveTime() {
        return keepAliveTime;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public BlockingQueue<Runnable> getWorkQueue() {
        return workQueue;
    }

    public RejectedExecutionHandler getHandler() {
        return handler;
    }
}
