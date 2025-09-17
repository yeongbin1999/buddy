package com.buddy.security.service

import com.buddy.domain.user.service.UserService
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    val userService: UserService
) : UserDetailsService {

    override fun loadUserByUsername(userId: String): CustomUserDetails =
        CustomUserDetails(userService.findById(userId.toInt()))
}