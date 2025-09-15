package com.buddy.global.security.jwt

import com.buddy.global.exception.ErrorCode
import com.buddy.security.exception.JwtAuthenticationException
import com.buddy.security.jwt.JwtProvider
import com.buddy.security.service.CustomUserDetails
import com.buddy.security.service.CustomUserDetailsService
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class JwtAuthenticationProvider (
    private val jwtTokenProvider: JwtProvider,
    private val customUserDetailsService: CustomUserDetailsService
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication {
        val token = authentication.credentials as String

        if (!jwtTokenProvider.validateToken(token)) {
            throw JwtAuthenticationException(ErrorCode.AUTH_INVALID_TOKEN)
        }

        val userId = jwtTokenProvider.getUserIdFromToken(token)
        val userDetails: CustomUserDetails = customUserDetailsService.loadUserByUsername(userId)

        return JwtAuthenticationToken(userDetails, userDetails.authorities)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return JwtAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}
