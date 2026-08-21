# ISSUE-009：超卖复现与乐观锁修复（Day 10）

> 对应讲义：`notes/day10-MySQL事务与锁/01-学习讲义.md` 第六节
> 涉及代码：`service/OrderService.java`、`entity/Product.java`

## 需求背景
库存 5 件的商品，并发 50 个请求抢购，卖出数量却超过 5 —— 超卖了。你要复现它，再用两种方案修复。

## 压测方法（没有 JMeter 也能压）
用 IDEA 写一个多线程 main 方法，或用以下 PowerShell/Git Bash 循环（50 并发）：
```bash
for i in {1..50}; do curl -s -X POST http://localhost:8080/buy/wrong/1 & done; wait
```
压完查库存：`SELECT id, name, stock, version FROM product WHERE id = 1;`
（压测前先把 id=1 的商品库存重置为 5：`UPDATE product SET stock=5 WHERE id=1;`）

## 任务清单

### Part 1：复现超卖
1. 重置库存为 5 → 压 `/buy/wrong/1`
2. 记录最终库存：____（预期：负数，超卖实锤）

### Part 2：原子 SQL 修复
3. 重置库存为 5 → 压 `/buy/atomic/1`
4. 记录最终库存：____（预期：恰好 0）
5. 思考：这个方案为什么不用加锁也安全？（提示：一条 SQL 的原子性）

### Part 3：乐观锁修复（TODO 在 OrderService.buyWithVersion）
6. 给 Product.version 加 `@Version`，完成 buyWithVersion 方法
7. 重置库存为 5 → 压 `/buy/version/1`
8. 记录最终库存：____（预期：0；部分请求抛 OptimisticLockException 属正常——那就是"没抢到的人"）
9. 打开 show-sql 观察乐观锁生成的 UPDATE 语句，把带 version 条件的那条复制到实验记录

## 验收标准
- [ ] 三组压测的库存结果记录（负 / 0 / 0）
- [ ] 能讲清三种方案的底层原理和适用场景
- [ ] 乐观锁的 UPDATE 语句记录在案

## 实验记录
（在这里记录库存数字和 SQL）

## 思考题
1. 乐观锁在高并发下大量请求失败重试，有什么副作用？怎么缓解？（提示：失败率、重试风暴）
2. 双窗口实验（讲义第七节）：隔离级别设为 RC 和 RR，同事务两次 SELECT 结果有何不同？记录你亲眼的观察。
