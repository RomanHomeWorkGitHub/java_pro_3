package org.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ThreadPollServiceTest {
    AtomicInteger counter;
    ThreadPollService threadPollService;


    @BeforeEach
    void setUp() throws InterruptedException {
        counter = new AtomicInteger(0);
        threadPollService = new ThreadPollService(3);
    }

    @RepeatedTest(5)
    void execute_ok() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            int taskId = i;
            threadPollService.execute(() -> {
                System.out.println(
                        "Thread: " + Thread.currentThread().getName() +
                                " taskId: " + taskId +
                                " result = " + counter.getAndIncrement()) ;
            });
            sleep(200);
        }
        assertEquals(5, counter.get());
    }

    @RepeatedTest(5)
    void shutdown_illegalStateException() {
        threadPollService.shutdown();

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            threadPollService.execute(() -> {});
        });

        assertEquals("Пул потоков отключен", exception.getMessage());
    }

    @RepeatedTest(5)
    void awaitTermination_ok() throws InterruptedException {
        Runnable task1 = () -> {
            try {
                Thread.sleep(100);
                System.out.println("Task1");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            counter.incrementAndGet();
        };
        Runnable task2 = () -> {
            try {
                Thread.sleep(100);
                System.out.println("Task2");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            counter.incrementAndGet();
        };
        Runnable task3 = () -> {
            try {
                Thread.sleep(100);
                System.out.println("Task3");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            counter.incrementAndGet();
        };
        threadPollService.execute(task1);
        threadPollService.execute(task2);
        threadPollService.awaitTermination();
        Thread.sleep(500);

        assertEquals(2, counter.get());

        threadPollService.execute(task3);
        Thread.sleep(500);
        assertEquals(3, counter.get());
    }
}