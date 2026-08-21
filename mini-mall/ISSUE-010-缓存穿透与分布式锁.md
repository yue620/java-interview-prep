# ISSUE-010：商品缓存、穿透修复与分布式锁（Day 11）

> 对应讲义：`notes/day11-Redis/01-学习讲义.md` 第三、五节
> 涉及代码：`service/ProductService.java`、`lock/RedisLock.java`
> 前置：本地 Redis 已启动（`redis-cli ping` 返回 PONG）

## 需求背景
商品详情接口 QPS 一上来数据库就抖。需要：加缓存 → 复现穿透攻击 → 修复 → 再手写一个分布式锁防止缓存击穿/超卖。

## 任务清单

### Part 1：Cache Aside 读缓存（完成 ProductService.getByIdWithCache 的 TODO）
1. 按代码里的 5 步提示实现
2. 验证：`GET /product/1` 调两次，第二次控制台**没有**新 SQL（show-sql 为证）
3. 也验证 redis-cli 里能看到 key：`KEYS product:*` 和 `TTL product:1`

### Part 2：复现并修复缓存穿透
4. 用不存在的 id 连刷 20 次：`for i in {1..20}; do curl -s http://localhost:8080/product/999999; done`
   观察控制台：每次都查了数据库 → 穿透实锤（记录观察）
5. 按"阶段三"提示加**缓存空对象**修复，再刷 20 次：数据库只查 1 次（记录观察）

### Part 3：手写分布式锁（完成 RedisLock.tryLock 的 TODO）
6. 完成 tryLock（setIfAbsent + 过期时间）
7. 写一个 `/buy/lock/{id}` 接口：先 tryLock，抢到才执行扣库存逻辑，finally 里 unlock
8. 压测验证不超卖
9. 实验：把过期时间设成 1 秒、业务里 sleep 3 秒，观察"锁提前过期"问题 → 引出 Redisson 看门狗的意义（口述即可）

## 验收标准
- [ ] 缓存命中证据（第二次无 SQL）
- [ ] 穿透复现 + 修复的对照记录
- [ ] 分布式锁压测不超卖；能讲清 UUID 比对和 Lua 的必要性

## 实验记录
（在这里记录每步的观察）

## 思考题
1. 为什么"更新数据时先更新数据库、再删缓存"而不是反过来？反过来的并发漏洞是什么？
2. 如果面试官问"你的锁在 Redis 主从切换时可能丢锁怎么办？"——了解 RedLock 争议即可，能说出"主从异步复制导致锁丢失，可用 RedLock 或接受最终一致+数据库兜底"就是高分。

## 挑战项
完成 ProductService 里"互斥锁重建缓存"的挑战 TODO，把 Part 3 的锁用在缓存重建场景（= 缓存击穿的互斥锁方案）。
