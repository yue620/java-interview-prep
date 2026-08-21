package com.example.mall;

import com.example.mall.entity.Product;
import com.example.mall.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Day 9 索引用：启动时检测数据量，不足 10 万条则批量插入测试数据
 * （插在启动流程里跑一次即可；跑完把 ENABLED 改成 false 避免每次启动检查）
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final boolean ENABLED = true;
    private static final int TOTAL = 100_000;

    private final ProductRepository productRepository;

    public DataSeeder(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (!ENABLED || productRepository.count() >= TOTAL) {
            return;
        }
        System.out.println("【DataSeeder】开始插入 " + TOTAL + " 条测试数据……");
        List<Product> batch = new ArrayList<>();
        for (int i = 1; i <= TOTAL; i++) {
            Product p = new Product();
            p.setName("商品" + i);
            p.setPrice(100 + i % 9000);
            p.setStock(100);
            p.setVersion(0);
            batch.add(p);
            if (batch.size() == 1000) {          // 分批插入，避免一次事务太大
                productRepository.saveAll(batch);
                batch.clear();
            }
        }
        productRepository.saveAll(batch);
        System.out.println("【DataSeeder】插入完成！跑完记得把 ENABLED 改成 false");
    }
}
