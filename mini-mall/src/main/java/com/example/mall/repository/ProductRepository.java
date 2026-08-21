package com.example.mall.repository;

import com.example.mall.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /** Day 9 索引实验用：按名字查（加索引前后 explain 对比这条 SQL） */
    Product findByName(String name);

    /**
     * ISSUE-009 超卖修复方案①：数据库原子扣减（推荐！）
     * 一条 SQL 完成"判断+扣减"，stock>0 条件不满足则影响行数=0 = 没抢到
     */
    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock - 1 WHERE p.id = :id AND p.stock > 0")
    int deductStock(@Param("id") Long id);
}
