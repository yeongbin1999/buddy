package com.buddy.common

import com.buddy.domain.user.entity.User
import com.buddy.domain.user.repository.UserRepository
import com.buddy.security.service.CustomUserDetails
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.RequestScope

@Component
@RequestScope
class Rq(
    private val request: HttpServletRequest,
    private val response: HttpServletResponse,
    private val userRepository: UserRepository
) {
    private var _user: User? = null

    val isLogin: Boolean
        get() = SecurityContextHolder.getContext().authentication?.principal is CustomUserDetails

    val user: User
        get() {
            if (_user == null && isLogin) {
                val principal = SecurityContextHolder.getContext().authentication.principal as CustomUserDetails
                _user = userRepository.findById(principal.id!!).orElseThrow {
                    RuntimeException("User not found in Rq for id: ${principal.id}")
                }
            }
            if (!isLogin) throw IllegalStateException("Login required.")
            return _user!!
        }

    fun getUserId(): Long {
        return user.id!!
    }
}