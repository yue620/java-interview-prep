package com.example.mall.entity;

import jakarta.persistence.*;

/**
 * 商品表 —— Day 9（索引）、Day 10（超卖/乐观锁）、Day 11（缓存）的主角
 */
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    /** 价格（单位：分，避免浮点误差） */
    private Integer price;

    /** 库存 */
    private Integer stock;

    /**
     * TODO(ISSUE-009)：加 @Version 注解 → JPA 自动实现乐观锁
     * 每次 update 自动执行 version = version + 1 并校验旧值
     */
    // @Version
    private Integer version;

    // ---- getter/setter（保持简单，不用 lombok） ----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
}
