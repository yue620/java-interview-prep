package com.example.mall;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ISSUE-009 压测工具：50 个线程同时抢购
 *
 * 使用步骤：
 *   1. 先启动 MallApplication（Tomcat 跑在 8080）
 *   2. MySQL 重置库存：UPDATE product SET stock = 5 WHERE id = 1;
 *   3. 运行本类的 main 方法（右键 → Run）
 *   4. 观察控制台统计，再查数据库：SELECT stock FROM product WHERE id = 1;
 *
 * 三个 URL 轮换着测，对比效果：
 *   /buy/wrong/1    → 预期：成功人数 > 5，库存变负数（超卖！）
 *   /buy/atomic/1   → 预期：恰好 5 人成功，库存为 0
 *   /buy/version/1  → 预期：恰好 5 人成功，库存为 0（失败的抛乐观锁异常，正常）
 */
public class StressTest {

    /** 要压的接口地址（换方案就改这里） */
    private static final String URL = "http://localhost:8080/buy/wrong/1";

    /** 并发线程数（= 同时有多少人在抢） */
    private static final int THREADS = 50;

    public static void main(String[] args) throws InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);

        AtomicInteger success = new AtomicInteger(0);   // 返回"购买成功"的人数
        AtomicInteger fail = new AtomicInteger(0);      // 返回"库存不足"或报错的人数

        // 发令枪：让 50 个线程都就位后同时开跑，模拟"同一瞬间抢购"
        CountDownLatch ready = new CountDownLatch(THREADS);  // 各线程到位计数
        CountDownLatch start = new CountDownLatch(1);        // 主线程发令

        for (int i = 0; i < THREADS; i++) {
            pool.submit(() -> {
                try {
                    ready.countDown();   // 我到位了
                    start.await();       // 等发令枪响

                    HttpRequest req = HttpRequest.newBuilder(URI.create(URL))
                            .POST(HttpRequest.BodyPublishers.noBody())
                            .build();
                    String body = client.send(req, HttpResponse.BodyHandlers.ofString()).body();

                    if (body.contains("购买成功")) {
                        success.incrementAndGet();
                    } else {
                        fail.incrementAndGet();
                    }
                } catch (Exception e) {
                    fail.incrementAndGet();   // 抛异常（如乐观锁冲突）也算没抢到
                }
            });
        }

        ready.await();              // 等 50 个线程全部就位
        long t0 = System.currentTimeMillis();
        start.countDown();          // 发令：一起冲！
        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);  // 等所有人跑完

        System.out.println("====================================");
        System.out.println("压测接口: " + URL);
        System.out.println("并发人数: " + THREADS);
        System.out.println("购买成功: " + success.get() + " 人");
        System.out.println("没抢到  : " + fail.get() + " 人");
        System.out.println("总耗时  : " + (System.currentTimeMillis() - t0) + " ms");
        System.out.println("====================================");
        System.out.println("现在去数据库执行: SELECT stock FROM product WHERE id = 1;");
        System.out.println("库存只有 5 件 → 成功人数 > 5 或库存为负数 = 超卖！");
    }
}
