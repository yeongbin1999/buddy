package com.buddy.global.config

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.data.redis.core.script.DefaultRedisScript

@EnableCaching
@Configuration
class BuddyRedisConfig {

    // Refresh Token 회전용 Lua 스크립트
    @Bean("refreshTokenRotationScript")
    fun refreshTokenRotationScript(): DefaultRedisScript<Long> =
        DefaultRedisScript<Long>().apply {
            setLocation(ClassPathResource("redis/refresh_token_rotate.lua"))
            resultType = Long::class.java
        }
}