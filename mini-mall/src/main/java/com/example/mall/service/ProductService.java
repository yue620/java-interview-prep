package com.example.mall.service;

import com.example.mall.entity.Product;
import com.example.mall.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * ISSUE-010：商品缓存 + 穿透修复 + 互斥锁重建
 */
@Service
public class ProductService {

    private static final String CACHE_KEY = "product:";
    private static final String LOCK_KEY = "lock:product:";

    private final ProductRepository productRepository;
    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProductService(ProductRepository productRepository, StringRedisTemplate redis) {
        this.productRepository = productRepository;
        this.redis = redis;
    }

    /**
     * 阶段一（无缓存）：直接查库 —— 先跑这个，show-sql 能看到每次都查数据库
     */
    public Product getByIdNoCache(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    /**
     * TODO(ISSUE-010 阶段二)：Cache Aside 读模式
     * 步骤提示：
     *   1. redis.opsForValue().get(CACHE_KEY + id)
     *   2. 命中：反序列化返回（注意：命中"空对象"标记也要处理，见阶段三）
     *   3. 未命中：查数据库
     *   4. 查到：序列化后 redis.opsForValue().set(key, json, Duration.ofSeconds(300))
     *   5. 查不到：直接返回 null（此时有穿透漏洞！用不存在的 id 狂刷接口验证）
     */
    public Product getByIdWithCache(Long id) throws Exception {
        // TODO：按上面 5 步实现
        return productRepository.findById(id).orElse(null);
    }

    /**
     * TODO(ISSUE-010 阶段三)：修复穿透 —— 缓存空对象
     * 在上面的方法里改：
     *   数据库查不到时，redis.set(key, "NULL", Duration.ofSeconds(30))
     *   读到 "NULL" 标记时直接返回 null，不再查库
     * 验证：再用不存在的 id 狂刷，数据库只被查一次
     */

    /**
     * TODO(ISSUE-010 挑战项)：热点 key 击穿 —— 互斥锁重建
     * 思路：
     *   缓存未命中时，先 redis.opsForValue().setIfAbsent(LOCK_KEY + id, "1", Duration.ofSeconds(10))
     *   抢到锁的线程才查库重建；没抢到的 sleep 50ms 后重新读缓存（自旋重试）
     *   finally 里释放锁（删 lock key）
     */
}
