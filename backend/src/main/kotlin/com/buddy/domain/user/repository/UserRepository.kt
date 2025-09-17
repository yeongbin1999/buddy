package com.buddy.domain.user.repository

import com.buddy.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByProviderAndProviderId(provider: String, providerId: String): User?
}
