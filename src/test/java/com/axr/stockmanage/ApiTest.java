package com.axr.stockmanage;

import com.axr.stockmanage.common.RedisIdWorker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author xinrui.an
 * @date 2025/02/10
 */
@SpringBootTest
class ApiTest {

    @Resource
    private RedisIdWorker worker;

    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    @Autowired
    private RedisIdWorker redisIdWorker;

    @Test
    void testIdWorker() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(300);
        Runnable task = () -> {
            for (int i = 0; i < 100; i++) {
                long id = redisIdWorker.nextId("order");
                System.out.println("id = " + id);
            }
            latch.countDown();
        };
        long start = System.currentTimeMillis();
        for (int i = 0; i < 300; i++) {
            executor.submit(task);
        }

        latch.await();
        long end = System.currentTimeMillis();

        System.out.println("time = " + (end - start));
        assertTrue((end - start) < 10000, "Test took too long: " + (end - start) + " ms");
    }

}
