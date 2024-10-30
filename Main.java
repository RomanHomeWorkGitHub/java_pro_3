package org.example.service;

import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        AtomicInteger res = new AtomicInteger();
        var threadPollService = new ThreadPollService(3);
        for (int i = 0; i < 10; i++) {
            int taskId = i;
            threadPollService.execute(() -> {
                System.out.println("Thread: " + currentThread().getName() +
                        " taskId: " + taskId +
                        " result = " + res.getAndIncrement());
            });
            sleep(100);
        }
    }
}

