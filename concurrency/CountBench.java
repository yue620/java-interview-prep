package concurrency;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * CountBench —— Day 5 实验二：三种计数方式压测
 *
 * 8 个线程 × 各 25 万次自增 = 期望 2,000,000
 * 对比 synchronized / AtomicInteger / LongAdder 的正确性与耗时
 *
 * 【实测数据】（跑完填这里）：
 *      synchronized   结果=2000000 耗时=44 ms
 *      AtomicInteger  结果=2000000 耗时=24 ms
 *      LongAdder      结果=2000000 耗时=22 ms
 *
 * 【思考题】为什么高竞争下 LongAdder 比 AtomicInteger 快？
 *   AtomicInteger 只有一个变量，所有线程 CAS 抢同一个值，竞争激烈时大量失败自旋。
 *   LongAdder 内部是 base + Cell 数组：多个计数单元分散竞争，各改各的，最后 sum() 求和。
 *   （实测两者接近的原因：8 线程竞争还不算极端，线程数越多 LongAdder 优势越明显；
 *     且 synchronized 仅 44ms 也说明 8 线程下锁竞争尚轻，无竞争锁优化生效。）
 */
public class CountBench {

    private static final int THREADS = 8;
    private static final int TIMES_PER_THREAD = 250_000;

    // ==================== 三种计数器（共享变量） ====================

    private static int syncCount = 0;                          // 配 synchronized 用
    private static final AtomicInteger atomicCount = new AtomicInteger(0);
    private static final LongAdder longAdder = new LongAdder();

    public static void main(String[] args) throws InterruptedException {
        long t1 = benchSynchronized();
        System.out.println("synchronized   结果=" + syncCount + " 耗时=" + t1 + " ms");

        long t2 = benchAtomic();
        System.out.println("AtomicInteger  结果=" + atomicCount.get() + " 耗时=" + t2 + " ms");

        long t3 = benchLongAdder();
        System.out.println("LongAdder      结果=" + longAdder.sum() + " 耗时=" + t3 + " ms");
    }

    // ==================== 已帮你写好的"并发执行框架" ====================

    /**
     * 通用压测模板：起 THREADS 个线程，各跑 task 任务，等全部结束，返回耗时
     * CountDownLatch 是"倒计时门闩"：8 个线程各 countDown 一次，
     * main 线程 await() 到计数归零才继续 —— 用来精确掐表。
     */
    private static long runBench(Runnable task) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(THREADS);
        long start = System.currentTimeMillis();
        for (int i = 0; i < THREADS; i++) {
            new Thread(() -> {
                task.run();
                latch.countDown();
            }).start();
        }
        latch.await();
        return System.currentTimeMillis() - start;
    }

    // ==================== TODO：三种方式的任务体 ====================

    /** TODO 1：synchronized 方式——对 CountBench.class 加锁，syncCount++ 循环 TIMES_PER_THREAD 次 */
    private static long benchSynchronized() throws InterruptedException {
        return runBench(() -> {
            // TODO：for 循环里 synchronized (CountBench.class) { syncCount++; }
            for (int i = 0; i < TIMES_PER_THREAD; i++) {
                synchronized(CountBench.class){
                    syncCount++;
                }
            }
        });
    }

    /** TODO 2：AtomicInteger 方式——atomicCount.incrementAndGet() 循环 */
    private static long benchAtomic() throws InterruptedException {
        return runBench(() -> {
            // TODO
            for (int i = 0; i < TIMES_PER_THREAD; i++) {
                atomicCount.incrementAndGet();
            }


        });
    }

    /** TODO 3：LongAdder 方式——longAdder.increment() 循环 */
    private static long benchLongAdder() throws InterruptedException {
        return runBench(() -> {
            // TODO
            for (int i = 0; i < TIMES_PER_THREAD; i++) {
                longAdder.increment();
            }
        });
    }
}
