package com.example.mall.service;

import com.example.mall.entity.Product;
import com.example.mall.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * ISSUE-007：@Transactional 失效场景复现（每种都在注释里写了解法）
 */
@Service
public class TxDemoService {

    private final ProductRepository productRepository;

    public TxDemoService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // ========== 场景 1：自调用失效 ==========

    /**
     * ❌ 失效：createWithSelfCall 没加事务，内部用 this 调了带事务的 doInsert
     * 异常抛出后数据依然入库 → 证明事务没生效
     * ✅ 解法：把 doInsert 挪到另一个 Service，或给外层方法也加 @Transactional
     */
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
            // 或 TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return "done";
    }

    // ========== 场景 3：checked 异常默认不回滚 ==========

    /**
     * ❌ 失效：抛出 checked 异常（Exception），默认不回滚
     * ✅ 解法：@Transactional(rollbackFor = Exception.class)
     */
    @Transactional   // TODO(修复)：加 (rollbackFor = Exception.class)
    public void createWithCheckedException(String name) throws Exception {
        Product p = new Product();
        p.setName(name);
        p.setPrice(100);
        p.setStock(10);
        productRepository.save(p);
        throw new Exception("checked 异常：默认不回滚！");
    }

    // ========== 加分实验：REQUIRES_NEW 保住日志 ==========

    /**
     * 模拟"主业务回滚但日志要留下"：把本方法标成 REQUIRES_NEW，
     * 由另一个带事务的方法调用它，外层回滚时这里的日志记录依然提交
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(String msg) {
        Product log = new Product();
        log.setName("LOG-" + msg);
        log.setPrice(0);
        log.setStock(0);
        productRepository.save(log);
    }
}
