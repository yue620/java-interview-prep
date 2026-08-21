package com.example.mall.circular;

/**
 * ISSUE-006 实验类：循环依赖
 *
 * 玩法（按 ISSUE-006 的步骤来）：
 *   第一步：给本类和 BService 都加上 @Service，用【构造方法注入】互相依赖 →
 *           启动，观察 BeanCurrentlyInCreationException 报错（构造循环依赖无解）
 *   第二步：改成【setter 注入】→ 启动成功，三级缓存生效了
 *   第三步：能对着三级缓存图把"A 先实例化 → B 从三级缓存拿 A 早期引用"讲一遍
 *
 * 注意：完成实验后把注解去掉（或保持 setter 版本），否则项目无法启动。
 */
public class AService {

    private BService b;

    // TODO(实验第一步)：加 @Service + 构造方法注入 BService，复现报错
    // public AService(BService b) { this.b = b; }

    // TODO(实验第二步)：改成 setter 注入，验证三级缓存解决循环依赖
    // @Autowired
    // public void setB(BService b) { this.b = b; }

    public String hello() {
        return "A 拿到了 B: " + (b != null);
    }
}
