# mini-mall：八股实践项目（Day 7 - Day 11）

一个最小化的 Spring Boot 商城，专门用来**亲手复现面试里的经典问题**：
每个 ISSUE 对应一天的学习，代码里留好了 TODO，做完 TODO 就完成了当天的实践。

## 环境要求

- JDK 17+（你的 JDK 23 可以直接用）
- 本机 MySQL：先建库 `CREATE DATABASE mini_mall DEFAULT CHARSET utf8mb4;`
- 本机 Redis：`redis-cli ping` 返回 PONG
- 修改 `src/main/resources/application.yml` 里的数据库账号密码

## 启动

```bash
mvn spring-boot:run
# 或在 IDEA 里直接跑 MallApplication
```

> 首次启动 DataSeeder 会自动插入 10 万条商品数据（Day 9 索引用），
> 插完后把 `DataSeeder.ENABLED` 改成 `false`。

## ISSUE 地图（按天做）

| 天 | ISSUE | 复现的经典问题 | 涉及代码 |
|---|---|---|---|
| Day 7 | ISSUE-006 | Bean 生命周期、循环依赖三级缓存、AOP | lifecycle/ circular/ aop/ |
| Day 8 | ISSUE-007 | @Transactional 三种失效场景 | service/TxDemoService |
| Day 9 | ISSUE-008 | 慢查询、explain、索引失效、最左前缀 | repository/ + 10w 数据 |
| Day 10 | ISSUE-009 | 超卖、原子 SQL、@Version 乐观锁 | service/OrderService |
| Day 11 | ISSUE-010 | 缓存穿透、缓存空对象、Redis 分布式锁 | service/ProductService、lock/RedisLock |

## 使用约定

1. 先读当天讲义（`notes/dayXX-*/01-学习讲义.md`）
2. 打开对应 ISSUE，按任务清单做，关键实现自己写（TODO 处）
3. 把实验观察记录在 ISSUE 文件的"实验记录"区
4. 完成后找 Kimi review + 模拟抽查，然后 commit
