package com.example.mall.service;

import com.example.mall.entity.Product;
import com.example.mall.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ISSUE-007：@Transactional 失效场景复现（每种都在注释里写了解法）
 */
@Service
public class TxDemoService {

    private final ProductRepository productRepository;
    //【修改】日志事务拆到独立的 Spring Bean，确保调用经过代理
    private final TxLogService txLogService;

    //【修改】注入 TxLogService，用它调用 REQUIRES_NEW 方法
    public TxDemoService(ProductRepository productRepository,
                         TxLogService txLogService) {
        this.productRepository = productRepository;
        this.txLogService = txLogService;
    }

    // ========== 场景 1：自调用失效 ==========

    /**
     * ❌ 失效：createWithSelfCall 没加事务，内部用 this 调了带事务的 doInsert
     * 异常抛出后数据依然入库 → 证明事务没生效
     * ✅ 解法：把 doInsert 挪到另一个 Service，或给外层方法也加 @Transactional
     */
    @Transactional
    public String createWithSelfCall(String name) {
        this.doInsert(name);   // this = 原始对象，绕过代理！
        return "done";
    }

    @Transactional
    public void doInsert(String name) {
        Product p = new Product();
        p.setName(name);
        p.setPrice(100);
        p.setStock(10);
        productRepository.save(p);
        throw new RuntimeException("故意抛异常：如果事务生效，这条数据不该入库");
    }

    // ========== 场景 2：异常被吞 ==========

    /**
     * ❌ 失效：异常被 catch 吃掉，代理感知不到 → 正常提交
     * ✅ 解法：catch 里继续 throw，或手动 setRollbackOnly()
     */
    @Transactional
    public String createWithSwallowedException(String name) {
        Product p = new Product();
        p.setName(name);
        p.setPrice(100);
        p.setStock(10);
        productRepository.save(p);
        try {
            int i = 1 / 0;
        } catch (Exception e) {
            System.out.println("异常被吞了: " + e.getMessage());
            // TODO(修复)：throw new RuntimeException(e);
            throw new RuntimeException(e);
            // 或 TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return "done";
    }

    // ========== 场景 3：checked 异常默认不回滚 ==========

    /**
     * ❌ 失效：抛出 checked 异常（Exception），默认不回滚
     * ✅ 解法：@Transactional(rollbackFor = Exception.class)
     */
    @Transactional(rollbackFor = Exception.class)   // TODO(修复)：加 (rollbackFor = Exception.class)
    public void createWithCheckedException(String name) throws Exception {
        //【修改】通过独立 Bean 调用，REQUIRES_NEW 才能被 Spring 代理拦截
        txLogService.saveLog(name);
        Product p = new Product();
        p.setName(name);
        p.setPrice(100);
        p.setStock(10);
        productRepository.save(p);
        throw new Exception("checked 异常：默认不回滚！");
    }

}
