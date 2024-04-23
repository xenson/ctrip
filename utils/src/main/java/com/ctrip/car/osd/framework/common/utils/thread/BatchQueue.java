package com.ctrip.car.osd.framework.common.utils.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * 批处理队列
 * 达到某个窗口期或者数量达到阈值执行任务
 *
 * @param <T>
 */
public class BatchQueue<T> {

    // 批处理数量阈值
    private final int size;
    // 批处理窗口期阈值
    private final int period;
    // 批处理具体逻辑
    private final Consumer<List<T>> consumer;
    private final AtomicBoolean isLooping = new AtomicBoolean(false);
    private final BlockingQueue<T> queue = new LinkedBlockingQueue<>();
    private final ExecutorService executorService;

    private AtomicLong start = new AtomicLong(System.currentTimeMillis());

    public BatchQueue(int size, int period, ExecutorService executorService, Consumer<List<T>> consumer) {
        this.size = size;
        this.period = period;
        this.consumer = consumer;
        this.executorService = executorService;
    }

    public BatchQueue(int size, ExecutorService executorService, Consumer<List<T>> consumer) {
        this(size, 500, executorService, consumer);
    }

    public boolean add(T t) {
        boolean result = queue.add(t);
        if (!isLooping.get() && result) {
            isLooping.set(true);
            startLoop();
        }
        return result;
    }

    public void completeAll() {
        while (!queue.isEmpty()) {
            consume();
        }
    }

    private void startLoop() {
        executorService.execute(() -> {
            start = new AtomicLong(System.currentTimeMillis());
            while (true) {
                long latest = System.currentTimeMillis() - start.get();
                if (queue.size() >= size || (!queue.isEmpty() && latest > period)) {
                    consume();
                } else if (queue.isEmpty()) {
                    isLooping.set(false);
                    break;
                }
            }
        });
    }

    private void consume() {
        List<T> drained = new ArrayList<>();
        int num = queue.drainTo(drained, size);
        if (num > 0) {
            consumer.accept(drained);
            start.set(System.currentTimeMillis());
        }
    }

}
