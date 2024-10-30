package org.example.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ThreadPollService {
    LinkedList<Runnable> taskQueue;
    AtomicBoolean isShutdown;

    public ThreadPollService(int capacity) throws InterruptedException {
      this.taskQueue = new LinkedList<>();
        this.isShutdown = new AtomicBoolean(false);

        for (int i = 0; i < capacity; i++) {
            System.out.println("Подготовка потока");
            Thread thread = new Thread(() -> {
                while (!isShutdown.get()) {
                    synchronized (taskQueue) {
                        Runnable next = taskQueue.poll();
                        if (next != null) {
                            next.run();
                        }
                    }
                }
            }, Thread.currentThread().getName() + " - " + i);
            thread.start();

            System.out.println("Поток: " + Thread.currentThread().getName() + " - " + i + " стартанул");
        }
    }

    public void execute(Runnable task) {
        if (isShutdown.get()) {
            throw new IllegalStateException("Пул потоков отключен");
        }
        synchronized (taskQueue) {
            taskQueue.addLast(task);
            taskQueue.notify();
        }
    }


    public void shutdown() {
        isShutdown.set(true);
        synchronized (taskQueue) {
            taskQueue.notifyAll();
        }
    }

    public void awaitTermination() throws InterruptedException {
        synchronized (taskQueue) {
            while (!taskQueue.isEmpty()) {
                taskQueue.wait();
            }
        }
    }
}
