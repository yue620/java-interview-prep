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
     * ❌ 错误示范：读库存 → 判断 → 盲扣，三步不是原子的
     * 并发压测 /buy/wrong 接口，库存会变成负数、成功人数远超库存
     *
     * 【2026-10-08 实测教训】最初的版本（读+save 绝对值写回）压测不出负数：
     *   ① 读和写之间没有业务耗时，危险窗口只有几毫秒，请求实际排队执行了
     *   ② JPA save 写的是算好的绝对值（最小 0），永远写不出负数
     * 所以改成：sleep 100ms 模拟真实业务耗时（拉大窗口）+ 盲扣 SQL（不带 stock>0 条件）
     */
    @Transactional
    public String buyWrong(Long id) {
        Product p = productRepository.findById(id).orElseThrow();
        if (p.getStock() > 0) {
            // 模拟真实业务中"读到库存"和"扣库存"之间的耗时（算价格、查优惠券……）
            // 正是这段耗时把危险窗口拉大，让并发线程都读到同一个旧库存
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            int remaining = p.getStock() - 1;
            productRepository.deductStockBlind(id);   // 盲扣：UPDATE stock=stock-1，不看当前值
            return "购买成功，剩余库存: " + remaining;
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
     * ✅ 修复方案②：乐观锁（@Version）
     *
     * 【2026-10-08 实测教训】第一次实现照抄了 buyWrong 里的 deductStockBlind，
     * 结果照样扣成负数 —— 因为 @Version 只对【实体 save()】生效：
     * JPA 会把 UPDATE 自动改成 WHERE id=? AND version=?；
     * 而 JPQL 的 @Modifying UPDATE 是你写什么执行什么，完全绕过乐观锁！
     *
     * 正确姿势：findById → setStock → save()，让 JPA 的脏检查 + @Version 接管。
     * 并发冲突时失败方抛 OptimisticLockException（影响行数=0），库存不会变负。
     *
     * 注意：这里故意【不加】sleep —— 如果所有线程同时读到 version=0，
     * 就只有 1 个人能提交成功（其余全部冲突失败），不利于演示"5 件库存卖完"。
     * 这也说明乐观锁的弱点：高冲突时失败率高，需要重试机制配合。
     */
    @Transactional
    public String buyWithVersion(Long id) {
        Product p = productRepository.findById(id).orElseThrow();
        if (p.getStock() > 0) {
            p.setStock(p.getStock() - 1);
            productRepository.save(p);   // ← 关键：实体 save，UPDATE 自动带 version 校验
            return "购买成功";
        }
        return "库存不足";
    }
}
