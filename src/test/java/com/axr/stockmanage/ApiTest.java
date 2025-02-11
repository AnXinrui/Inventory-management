package com.axr.stockmanage;

import com.axr.stockmanage.common.RedisIdWorker;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

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
    private static final Logger log = LoggerFactory.getLogger(ApiTest.class);

    private static final int THREAD_COUNT = 300; // 线程数
    private static final int IDS_PER_THREAD = 100; // 每个线程生成 ID 的数量

    private final ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
    private final RedisIdWorker redisIdWorker = new RedisIdWorker();

    @Test
    void testIdWorker() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        Runnable task = () -> {
            for (int i = 0; i < IDS_PER_THREAD; i++) {
                long id = redisIdWorker.nextId("order");
                log.info("Generated ID: {}", id); // 使用 logger 代替 System.out
            }
            latch.countDown();
        };

        long start = System.currentTimeMillis();

        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(task);
        }

        latch.await(); // 等待所有线程完成
        long end = System.currentTimeMillis();
        long duration = end - start;

        log.info("Test completed in {} ms", duration);
        assertTrue(duration < 10000, "Test took too long: " + duration + " ms");
    }
}