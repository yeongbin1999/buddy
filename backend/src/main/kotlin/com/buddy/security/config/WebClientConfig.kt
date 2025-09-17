package com.buddy.security.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {
    // 공통 WebClient Builder
    @Bean
    fun webClientBuilder(): WebClient.Builder = WebClient.builder()
}