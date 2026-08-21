# ISSUE-008：慢查询优化 —— 索引与 explain 实战（Day 9）

> 对应讲义：`notes/day09-MySQL索引/01-学习讲义.md` 第五节
> 前置：启动一次应用，DataSeeder 会自动插入 10 万条商品数据（插完把 ENABLED 改 false）

## 需求背景
运营反馈"按商品名搜索特别慢"。你需要用 explain 定位问题、加索引解决、并验证三个经典失效场景。

## 任务清单

### Part 1：复现慢查询
1. MySQL 客户端执行（记录耗时）：
   ```sql
   SELECT * FROM product WHERE name = '商品99999';
   ```
2. `EXPLAIN` 这条 SQL，记录：`type=____`、`key=____`、`rows=____`

### Part 2：加索引 + 对比
3. `ALTER TABLE product ADD INDEX idx_name(name);`
4. 再跑同样的查询和 EXPLAIN，记录：`type=____`、`key=____`、`rows=____`
5. 计算：扫描行数从多少降到多少？

### Part 3：索引失效三连（每条都先 EXPLAIN 预测，再执行验证）
6. 函数失效：`WHERE LEFT(name, 2) = '商品'`
7. 隐式转换失效：给表加一个 `phone VARCHAR(20)` 列填些数据，然后 `WHERE phone = 13800138000`（不加引号）
8. like 前缀失效：`WHERE name LIKE '%9999'`，对比 `WHERE name LIKE '商品99%'`

### Part 4：联合索引与最左前缀
9. `ALTER TABLE product ADD INDEX idx_name_price(name, price);`
10. 分别 EXPLAIN 三条 SQL，记录 key 和 key_len，判断各用了几列索引：
    ```sql
    SELECT * FROM product WHERE name='商品100' AND price=200;
    SELECT * FROM product WHERE price=200;
    SELECT * FROM product WHERE name='商品100';
    ```

## 验收标准
- [ ] Part 1/2 的 explain 前后对比记录（type 从 ALL 变 ref，rows 骤降）
- [ ] Part 3 三个失效场景的 explain 记录
- [ ] Part 4 能说出每条 SQL 用了联合索引的几列、为什么

## 实验记录
（在这里贴 explain 结果表格）

## 思考题
1. 为什么 `SELECT id, name FROM product WHERE name = '商品100'` 比 `SELECT *` 更好？（提示：回表）
2. 给"性别"这种只有几个值的列建索引有意义吗？为什么？
