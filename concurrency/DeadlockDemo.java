package concurrency;

/**
 * DeadlockDemo —— Day 3 实践：死锁复现、jstack 排查与修复
 *
 * 对应需求：concurrency/ISSUE-003-死锁实验.md
 *
 * 【jstack 死锁报告粘贴区】（任务 2 完成后贴在这里）：
 *
 *
 * 【报告分析】：哪两行说明"互相持有对方要的锁"？
 *
 *
 * 【线上排查死锁流程总结】（任务 4）：
 *
 */
public class DeadlockDemo {

    private static final Object lockA = new Object();
    private static final Object lockB = new Object();

    public static void main(String[] args) {
        // 任务 1：先让 createDeadlock() 跑起来，程序会卡死
        createDeadlock();

        // 任务 3：修复后改用这行（把上一行注释掉），程序应正常结束
        // fixDeadlock();
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
    private static void createDeadlock() {
        // TODO

    }

    /**
     * TODO 3：修复死锁
     *
     * 要求：两个线程都按 lockA → lockB 的固定顺序加锁。
     * 思考：这破坏了死锁四个必要条件中的哪一个？（写在 ISSUE 思考题里）
     */
    private static void fixDeadlock() {
        // TODO

    }
}
