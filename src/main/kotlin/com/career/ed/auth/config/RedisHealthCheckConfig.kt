package com.career.ed.auth.config

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory

@Configuration
class RedisHealthCheckConfig {

    @Bean
    fun redisPing(factory: ReactiveRedisConnectionFactory): CommandLineRunner {
        return CommandLineRunner {
            factory.reactiveConnection
                .ping()
                .doOnNext { println("Redis is reachable: $it") }
                .doOnError { println("Redis connection failed: ${it.message}") }
                .subscribe()
        }
    }
}