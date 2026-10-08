package com.example.mall.service;

import com.example.mall.entity.Product;
import com.example.mall.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * ISSUE-007 加分实验：独立的日志事务。
 */
@Service //【新增】注册为独立 Spring Bean，让调用可以经过 AOP 代理
public class TxLogService {

    private final ProductRepository productRepository;

    public TxLogService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * 外层业务回滚时，日志事务仍然独立提交。
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW) //【新增】挂起外层事务，开启并提交新事务
    public void saveLog(String msg) {
        Product log = new Product();
        log.setName("LOG-" + msg);
        log.setPrice(0);
        log.setStock(0);
        productRepository.save(log);
    }
}
