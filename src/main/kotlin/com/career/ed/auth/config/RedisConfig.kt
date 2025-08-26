package com.career.ed.auth.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer


@Configuration
class RedisConfig {
    @Bean
    fun objectMapper(): ObjectMapper = jacksonObjectMapper()


    // Let Spring Boot autoconfigure the factory (no duplicate bean name!)
    @Bean
    fun reactiveRedisTemplate(factory: ReactiveRedisConnectionFactory, mapper: ObjectMapper): ReactiveRedisTemplate<String, Any> {
        val keySer = StringRedisSerializer()
        val valSer = GenericJackson2JsonRedisSerializer(mapper)
        val context = RedisSerializationContext
            .newSerializationContext<String, Any>(keySer)
            .value(valSer)
            .hashKey(keySer)
            .hashValue(valSer)
            .build()
        return ReactiveRedisTemplate(factory, context)
    }
}