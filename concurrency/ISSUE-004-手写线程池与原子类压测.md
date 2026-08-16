# ISSUE-004：手写简化线程池 + 三种计数方式压测

> 阶段 2 · Day 5（8/17）实践任务
> 对应八股：线程池工作原理、CAS 原子类、synchronized 性能对比

---

## 实验一：手写一个简化线程池（`concurrency/MyThreadPool.java`，骨架已给）

### 需求描述

不用任何 JUC 线程池类，自己实现一个迷你线程池，理解"固定员工 + 任务队列"的本质：

- 构造时指定线程数 N，启动 N 个"员工线程"
- 每个员工循环从**阻塞队列**里取任务执行（取不到就阻塞等待）
- 提供 `execute(Runnable)` 提交任务

### 接口要求

```java
public class MyThreadPool {
    public MyThreadPool(int workerCount);   // 创建并启动 N 个员工
    public void execute(Runnable task);      // 提交任务进队列
}
```

### 验收标准

1. 队列用 `LinkedBlockingQueue<Runnable>`（JUC 提供的阻塞队列，直接用——它的 `take()` 方法"队列空时阻塞等待"正好是你需要的）
2. 提交 10 个任务到 3 人线程池，打印任务执行的线程名
3. **验收点**：10 个任务只由 3 个线程名轮流打印出来

### 思考题（注释里回答）

1. 你的"员工线程"为什么不会跑完一个任务就结束？（看 while 循环的作用）
2. `take()` 和 `poll()` 的区别？为什么这里要用 take？
3. 你的 MyThreadPool 和真正的 ThreadPoolExecutor 差了什么？（至少说出 3 点：核心/临时工区分、拒绝策略、空闲回收、异常处理……）

---

## 实验二：三种计数方式压测（`concurrency/CountBench.java`，骨架已给）

### 需求

8 个线程各执行 25 万次自增，分别用三种方式，计时对比：

1. **synchronized** 同步块
2. **AtomicInteger**（CAS）
3. **LongAdder**（分段计数）

### 验收标准

- 三个结果都必须是 2,000,000（验证正确性）
- 记录三个耗时到注释里，观察 LongAdder 和 AtomicInteger 的差距
- 注释里回答：为什么高竞争下 LongAdder 比 AtomicInteger 快？（提示：CAS 自旋 vs 分段分散竞争）

## 提交要求

```bash
git add -A
git commit -m "feat: day5 手写简化线程池 + 三种计数方式压测"
git push
```
