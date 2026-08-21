package com.example.mall.service;

import com.example.mall.entity.Product;
import com.example.mall.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ISSUE-009：超卖复现与修复
 *
 * buyWrong()：故意写的错误扣库存（读-判-写三步分开，并发下必超卖）
 * buyWithAtomicSql()：修复方案①，数据库原子扣减
 * buyWithVersion()：TODO 修复方案②，乐观锁（先给 Product.version 加 @Version）
 */
@Service
public class OrderService {

    private final ProductRepository productRepository;

    public OrderService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * ❌ 错误示范：读库存 → 判断 → 扣减，三步不是原子的
     * 并发压测 /buy/wrong 接口，库存会变成负数
     */
    @Transactional
    public String buyWrong(Long id) {
        Product p = productRepository.findById(id).orElseThrow();
        if (p.getStock() > 0) {
            p.setStock(p.getStock() - 1);   // 两个线程都读到 1，都通过判断 → 超卖
            productRepository.save(p);
            return "购买成功，剩余库存: " + p.getStock();
        }
        return "库存不足";
    }

    /**
     * ✅ 修复方案①：原子 SQL（Day 10 讲义第六节方案 1）
     * 判断和扣减合成一条 SQL，数据库层面原子执行
     */
    @Transactional
    public String buyWithAtomicSql(Long id) {
        int rows = productRepository.deductStock(id);
        return rows > 0 ? "购买成功" : "库存不足（原子扣减拦住了超卖）";
    }

    /**
     * TODO(ISSUE-009) 修复方案②：乐观锁
     * 步骤：
     *   1. 给 Product.version 字段加上 @Version
     *   2. 这里照抄 buyWrong 的"读-改-save"写法即可
     *      —— 有 @Version 后，JPA 生成的 UPDATE 会自动带 version 校验，
     *         并发冲突时抛 OptimisticLockException，库存不会变负
     *   3. 压测验证：库存恰好扣到 0，不为负（部分请求抛异常=没抢到，属正常）
     */
    @Transactional
    public String buyWithVersion(Long id) {
        // TODO：参考 buyWrong，体会"代码几乎一样，但 @Version 在背后保护了你"
        return "TODO";
    }
}
