package com.ctrip.car.util.thread;

import com.ctrip.car.osd.framework.common.utils.thread.BatchQueue;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BatchQueueTest {
    private static BatchQueue<String> batchQueue = new BatchQueue<>(3, 1000, Executors.newCachedThreadPool(), BatchQueueTest::process);

    public static void main(String[] args) {
        while (true) {
            String line = new Scanner(System.in).nextLine();
            if (line.equals("done")) {
                batchQueue.completeAll();
                break;
            }
            batchQueue.add(line);
        }
    }

    private static void process(List<String> x) {
        System.out.println("处理数据：" + x);
    }
}
