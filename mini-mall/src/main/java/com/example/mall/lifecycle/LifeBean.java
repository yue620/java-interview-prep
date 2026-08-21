package com.example.mall.lifecycle;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

/**
 * Day 7 实验：观察 Bean 生命周期
 * 启动应用看控制台打印顺序：1 → 2；停止应用时打印 3
 */
@Component
public class LifeBean {

    public LifeBean() {
        System.out.println("【1. 构造方法】对象被 new 出来了（此时依赖还没注入）");
    }

    @PostConstruct
    public void init() {
        System.out.println("【2. @PostConstruct】依赖注入完毕，初始化完成");
    }

    @PreDestroy
    public void onDestroy() {
        System.out.println("【3. @PreDestroy】容器关闭，Bean 即将销毁");
    }
}
