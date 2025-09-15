package com.buddy.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

@EnableJpaAuditing
@Configuration
class JpaAuditingConfig {

    // createdAt/updatedAt을 UTC로 강제
    @Bean
    fun dateTimeProvider(): DateTimeProvider =
        DateTimeProvider { Optional.of(OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime()) }
}