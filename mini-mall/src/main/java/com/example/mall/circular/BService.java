package com.example.mall.circular;

/**
 * ISSUE-006 实验类：与 AService 配对使用，步骤见 AService 注释
 */
public class BService {

    private AService a;

    // TODO(实验第一步)：加 @Service + 构造方法注入 AService
    // public BService(AService a) { this.a = a; }

    // TODO(实验第二步)：改成 setter 注入
    // @Autowired
    // public void setA(AService a) { this.a = a; }

    public String hello() {
        return "B 拿到了 A: " + (a != null);
    }
}
