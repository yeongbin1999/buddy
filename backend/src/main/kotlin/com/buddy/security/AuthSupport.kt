package com.buddy.security

import org.springframework.security.core.context.SecurityContextHolder

object AuthSupport {
    fun currentUserId(): Long {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null && authentication.isAuthenticated && authentication.name != "anonymousUser") {
            return authentication.name.toLong()
        }
        throw IllegalStateException("User not authenticated")
    }
}