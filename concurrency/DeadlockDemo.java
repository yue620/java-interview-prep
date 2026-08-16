package concurrency;

/**
 * DeadlockDemo —— Day 3 实践：死锁复现、jstack 排查与修复
 *
 * 对应需求：concurrency/ISSUE-003-死锁实验.md
 *
 * 【jstack 死锁报告】（2026-08-16 实测，jps -l 找到进程 136160 后 jstack 136160）：
 *
 *   Found one Java-level deadlock:
 *   =============================
 *   "线程1":
 *     waiting to lock monitor 0x000001d15d525370 (object 0x000000071c39d510, a java.lang.Object),
 *     which is held by "线程2"
 *   "线程2":
 *     waiting to lock monitor 0x000001d15d523f20 (object 0x000000071c39d500, a java.lang.Object),
 *     which is held by "线程1"
 *
 *   Java stack information for the threads listed above:
 *   ===================================================
 *   "线程1":
 *           at concurrency.DeadlockDemo$1.run(DeadlockDemo.java:53)
 *           - waiting to lock <0x000000071c39d510> (a java.lang.Object)
 *           - locked <0x000000071c39d500> (a java.lang.Object)
 *   "线程2":
 *           at concurrency.DeadlockDemo$2.run(DeadlockDemo.java:69)
 *           - waiting to lock <0x000000071c39d500> (a java.lang.Object)
 *           - locked <0x000000071c39d510> (a java.lang.Object)
 *
 *   Found 1 deadlock.
 *
 * 【报告分析】：
 *   关键证据是两组对应行——"线程1 waiting to lock d510, which is held by 线程2"
 *   与 "线程2 waiting to lock d500, which is held by 线程1"。
 *   线程1 持有 lockA(d500) 在等 lockB(d510)；线程2 持有 lockB(d510) 在等 lockA(d500)，
 *   互相持有对方需要的锁，形成等待环 → 死锁。
 *   两个线程状态均为 BLOCKED (on object monitor)，jstack 直接给出了卡住的代码行号
 *   （53 行和 69 行，即两个 synchronized 内层嵌套处），排查时可直接定位。
 *
 * 【线上排查死锁流程总结】：
 *   1. 发现服务无响应 / CPU 异常 / 接口超时
 *   2. jps -l 找到 Java 进程号 → jstack <pid> 抓线程快照
 *   3. 先看末尾有没有 "Found one Java-level deadlock"；没有则搜大量 BLOCKED 状态的线程
 *   4. 按报告中的类名+行号定位互相嵌套加锁的代码
 *   5. 修复：固定加锁顺序（破坏循环等待）/ tryLock 超时放弃 / 减小锁粒度
 *   心得：jstack 会自动检测死锁并直接报告"谁持有谁等待"和具体行号，
 *         给线程起有意义的名字能让报告可读性翻倍。
 */
public class DeadlockDemo {

    private static final Object lockA = new Object();
    private static final Object lockB = new Object();

    public static void main(String[] args) throws InterruptedException {
        // 任务 1：先让 createDeadlock() 跑起来，程序会卡死
//        createDeadlock();


        // 任务 3：修复后改用这行（把上一行注释掉），程序应正常结束
         fixDeadlock();
    }

    /**
     * TODO 1：制造死锁
     *
     * 线程1：synchronized(lockA) { 睡 100ms; synchronized(lockB) { 打印完成 } }
     * 线程2：synchronized(lockB) { 睡 100ms; synchronized(lockA) { 打印完成 } }
     *
     * 提示：sleep 是为了让两个线程都先拿到自己的第一把锁，
     *       确保"互相等待"的局面必然出现（回忆：sleep 释放锁吗？）
     * 两个线程都给个名字（new Thread(runnable, "线程1")），jstack 报告里好认。
     */
    private static void createDeadlock()  {
        // TODO
        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                synchronized (lockA){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    synchronized (lockB){
                        System.out.println("打印完成A");
                    }
                }
            }
        };

        Runnable r2 = new Runnable() {
            @Override
            public void run() {
                synchronized (lockB){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    synchronized (lockA){
                        System.out.println("打印完成B");
                    }
                }
            }
        };

        Thread t1 = new Thread(r1,"线程1");
        Thread t2 = new Thread(r2,"线程2");

        t1.start();
        t2.start();


    }

    /**
     * TODO 3：修复死锁
     *
     * 要求：两个线程都按 lockA → lockB 的固定顺序加锁。
     * 思考：这破坏了死锁四个必要条件中的哪一个？（写在 ISSUE 思考题里）
     */
    private static void fixDeadlock() {
        // TODO
        Runnable r1 = () -> {
            synchronized (lockA){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (lockB){
                    System.out.println("打印完成A");
                }
            }
        };

        Runnable r2 = () -> {
            synchronized (lockA){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (lockB){
                    System.out.println("打印完成B");
                }
            }
        };

        Thread t1 = new Thread(r1,"线程1");
        Thread t2 = new Thread(r2,"线程2");

        t1.start();
        t2.start();


    }
}
