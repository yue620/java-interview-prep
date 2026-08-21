package com.example.mall.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Day 7 实验：AOP 切面 —— 统计 service 包所有方法的耗时
 * 启动后访问任意接口，控制台会打印方法耗时
 */
@Aspect
@Component
public class TimeAspect {

    @Around("execution(* com.example.mall.service..*.*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = pjp.proceed();   // 真正执行目标方法
        System.out.println("【AOP】" + pjp.getSignature().toShortString()
                + " 耗时 " + (System.currentTimeMillis() - start) + "ms");
        return result;
    }
}
