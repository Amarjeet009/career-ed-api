package com.career.ed.common.service

import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Duration

@Service
class RedisService(
    private val redisTemplate: ReactiveStringRedisTemplate
) {

    private val ops = redisTemplate.opsForValue()

    /**
     * Save a key-value pair with TTL
     */
    fun set(key: String, value: String, ttl: Duration): Mono<Boolean> {
        return ops.set(key, value, ttl)
    }

    /**
     * Get value by key
     */
    fun get(key: String): Mono<String?> {
        return ops.get(key)
    }

    /**
     * Delete a key
     */
    fun delete(key: String): Mono<Boolean> {
        return redisTemplate.delete(key).map { it > 0 }
    }

    /**
     * Check if key exists
     */
    fun exists(key: String): Mono<Boolean> {
        return redisTemplate.hasKey(key)
    }

    /**
     * Refresh TTL without overwriting value
     */
    fun expire(key: String, ttl: Duration): Mono<Boolean> {
        return redisTemplate.expire(key, ttl)
    }
}