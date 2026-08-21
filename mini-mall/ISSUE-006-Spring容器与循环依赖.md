# ISSUE-006：复现并解决循环依赖（Day 7）

> 对应讲义：`notes/day07-Spring核心/01-学习讲义.md` 第四节
> 涉及代码：`circular/AService.java`、`circular/BService.java`、`lifecycle/LifeBean.java`、`aop/TimeAspect.java`

## 需求背景
线上启动报错 `BeanCurrentlyInCreationException`，排查发现是新人写出了 A 依赖 B、B 依赖 A 的循环依赖。你需要复现这个故障、理解 Spring 三级缓存为什么能（以及什么时候不能）解决它。

## 任务清单

### Part 1：生命周期与单例（热身）
1. 启动应用，确认 LifeBean 打印出【1. 构造方法】→【2. @PostConstruct】
2. 在 MallApplication.main 里取消注释 getBean 实验，验证两次取到同一对象
3. 停掉应用（IDEA 红色停止按钮），观察【3. @PreDestroy】是否打印

### Part 2：复现构造方法循环依赖报错
4. 给 AService、BService 加 `@Service`，都改成构造方法互相注入
5. 启动 → 记录报错信息（截图/复制到本文件下方"实验记录"）

### Part 3：用 setter 注入修复
6. 改成 `@Autowired` setter 注入 → 启动成功
7. 口述：三级缓存里 A 和 B 分别经历了什么？（对照讲义流程图）

### Part 4：AOP 验证
8. 访问 `GET http://localhost:8080/product/1`，确认控制台有【AOP】耗时打印

## 验收标准
- [ ] 构造循环依赖的报错原文记录在本文件
- [ ] setter 版本启动成功
- [ ] 能脱稿讲三级缓存流程

## 实验记录
（在这里粘贴报错和你的观察）

## 思考题
1. 为什么构造方法注入的循环依赖连三级缓存都救不了？
2. 三级缓存里的"对象工厂"为什么能解决 AOP 代理问题（提示：如果二级缓存直接放裸对象，代理对象哪来的）？
