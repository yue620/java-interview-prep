# ISSUE-007：@Transactional 失效场景复现与修复（Day 8）

> 对应讲义：`notes/day08-Spring事务与Boot/01-学习讲义.md` 第一节
> 涉及代码：`service/TxDemoService.java` + `controller/ProductController.java` 的 /tx/* 接口

## 需求背景
测试同学发现：代码里明明加了 `@Transactional`，出错后数据却入库了。你要复现三种典型失效场景，并用数据库记录证明"事务没生效"，再修复并证明"修好了"。

## 判定方法（每种场景通用）
1. 调接口前：`SELECT COUNT(*) FROM product WHERE name LIKE '%商品名%';`
2. 调接口（会抛异常或打印异常）
3. 再查一次 count：**多了 1 行 = 事务失效（没回滚）；不变 = 回滚成功**

## 任务清单

### 场景 1：自调用（GET /tx/self-call）
- 复现：接口返回 done，数据库多了"自调用商品" → 失效 ✅ 复现成功
- 修复：把 `doInsert` 拆到新 Service（如 `TxHelperService`）注入调用，再验证回滚

### 场景 2：异常被吞（GET /tx/swallow）
- 复现：数据库多了"吞异常商品" → 失效
- 修复：catch 里 `throw new RuntimeException(e)`（或按注释用 setRollbackOnly），再验证

### 场景 3：checked 异常（GET /tx/checked）
- 复现：接口 500，但数据库多了"checked异常商品" → 失效
- 修复：加 `rollbackFor = Exception.class`，再验证

### 加分：REQUIRES_NEW 保日志
- 写一个带事务的外层方法：先调 `saveLog("xxx")`，再抛异常回滚
- 验证：日志行入库了，业务行没有 → 能讲清为什么

## 验收标准
- [ ] 三种场景"失效证据 + 修复后回滚证据"各一组（count 查询结果记录）
- [ ] 每个场景能一句话说清根因（全部指向"AOP 代理"）

## 实验记录
（在这里记录每组的 count 结果）

## 思考题
1. 为什么"给外层方法也加 @Transactional"也能修好场景 1？（提示：加入同一个事务）
2. Spring 为什么默认不对 checked 异常回滚？（设计哲学的角度想想）
