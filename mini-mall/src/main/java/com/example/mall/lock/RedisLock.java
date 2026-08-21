package com.example.mall.lock;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * ISSUE-010：手写 Redis 分布式锁（SET NX PX + UUID + Lua 解锁）
 */
@Component
public class RedisLock {

    private final StringRedisTemplate redis;

    public RedisLock(StringRedisTemplate redis) {
        this.redis = redis;
    }

    /**
     * 抢锁
     * TODO：用一条命令完成"不存在才能设置 + 自动过期"
     * 提示：redis.opsForValue().setIfAbsent(key, value, Duration)
     *       —— 它底层就是 SET key value NX PX，原子的
     *
     * @param key         锁名，如 "lock:seckill:product:1"
     * @param expireSeconds 过期时间（防持锁者宕机导致死锁）
     * @return 锁的值（UUID，解锁时要比对）; null 表示没抢到
     */
    public String tryLock(String key, long expireSeconds) {
        // TODO：生成 UUID → setIfAbsent → 成功返回 UUID，失败返回 null
        return null;
    }

    /**
     * 解锁：只删自己的锁
     * TODO：为什么必须比对 value？（提示：锁过期后别人可能已抢到同名锁）
     * 为什么用 Lua？（提示：GET 比对 + DEL 两步要原子，否则比对完锁刚好过期被删）
     */
    public void unlock(String key, String value) {
        String lua = """
                if redis.call('get', KEYS[1]) == ARGV[1] then
                    return redis.call('del', KEYS[1])
                else
                    return 0
                end
                """;
        redis.execute(new DefaultRedisScript<>(lua, Long.class), List.of(key), value);
    }
}
