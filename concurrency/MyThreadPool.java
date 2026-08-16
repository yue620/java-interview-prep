package concurrency;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * MyThreadPool —— Day 5 实验一：手写简化线程池
 *
 * 对应需求：concurrency/ISSUE-004-手写线程池与原子类压测.md
 *
 * 核心思想：固定几个"员工线程" + 一个阻塞任务队列。
 * 员工循环从队列 take() 任务执行；队列空时 take() 会阻塞等待。
 */
public class MyThreadPool {

    /** 任务队列：提交的任务都进这里，员工从这里取 */
    private final LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

    /**
     * TODO 1：构造方法——创建并启动 workerCount 个员工线程
     *
     * 员工线程的逻辑（写成 while 循环）：
     *   while (true) {
     *       Runnable task = queue.take();   // 队列空就阻塞在这，不耗 CPU
     *       task.run();
     *   }
     *
     * 提示 1：take() 会抛 InterruptedException，catch 住打印即可
     * 提示 2：给员工线程起名字 "worker-0"、"worker-1"...（验收要看线程名）
     * 提示 3：思考——为什么员工跑完一个任务不会"下班"？（while 循环）
     */
    public MyThreadPool(int workerCount) {
        // TODO

    }

    /**
     * TODO 2：提交任务——把 task 放进队列
     *
     * 提示：queue 的 add / offer / put 都能放，试试 put（满了会阻塞）
     *       put 会抛 InterruptedException，用 try-catch 包一层，
     *       catch 里恢复中断标记：Thread.currentThread().interrupt();
     */
    public void execute(Runnable task) {
        // TODO

    }

    // ==================== 验收（已写好） ====================

    public static void main(String[] args) {
        MyThreadPool pool = new MyThreadPool(3);   // 3 个员工

        for (int i = 0; i < 10; i++) {           // 提交 10 个任务
            int taskId = i;
            pool.execute(() -> {
                System.out.println(Thread.currentThread().getName()
                        + " 执行任务 " + taskId);
                try { Thread.sleep(100); } catch (InterruptedException e) { }
            });
        }

        // 验收：10 个任务应只由 worker-0/1/2 三个线程轮流执行
        System.out.println("main：10 个任务已全部提交");
    }
}
