package com.example.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MallApplication {

    public static void main(String[] args) {
        var ctx = SpringApplication.run(MallApplication.class, args);
        // Day 7 实验：从容器取两次同一个 Bean，验证默认单例
        // Object s1 = ctx.getBean("lifeBean");
        // Object s2 = ctx.getBean("lifeBean");
        // System.out.println("两次取到的是同一个对象吗: " + (s1 == s2));
    }
}
