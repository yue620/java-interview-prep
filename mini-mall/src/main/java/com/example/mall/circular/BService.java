package com.example.mall.circular;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ISSUE-006 实验类：与 AService 配对使用，步骤见 AService 注释
 */
@Service
public class BService {

    private AService a;

    // TODO(实验第一步)：加 @Service + 构造方法注入 AService
//     public BService(AService a) { this.a = a; }

    // setter 注入允许 B 先完成实例化，再接收 A 的早期引用
//    @Autowired
//    public void setA(AService a) {
//        this.a = a;
//    }

    // 创建 B 时填充属性，发现依赖 A
    // 从 A 的三级缓存 ObjectFactory 获取 A 的早期引用
    // 早期引用进入二级缓存并注入 B；B 初始化完成后进入一级缓存

    public String hello() {
        return "B 拿到了 A: " + (a != null);
    }
}
