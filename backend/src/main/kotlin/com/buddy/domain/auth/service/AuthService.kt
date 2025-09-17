package com.buddy.domain.auth.service

import com.buddy.domain.user.entity.User
import com.buddy.domain.user.repository.UserRepository
import com.buddy.security.jwt.TokenDto
import com.buddy.security.service.CustomUserDetails
import com.buddy.security.service.TokenService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val tokenService: TokenService
) {
    fun logout(accessToken: String, deviceId: String) {
        tokenService.logout(extractUserId(accessToken), deviceId)
    }

    fun reissue(refreshToken: String, deviceId: String): TokenDto {
        return tokenService.reIssueTokens(refreshToken, deviceId)
    }

    private fun extractUserId(accessToken: String): Int {
        return tokenService.extractUserId(accessToken)
    }

    @Transactional
    fun findOrCreateOAuthUser(
        providerId: String,
        email: String,
        nickname: String,
        profileImageUrl: String?,
        provider: String
    ): CustomUserDetails {
        val user = userRepository.findByProviderAndProviderId(provider, providerId)
            ?: userRepository.save(
                User(
                    provider = provider,
                    providerId = providerId,
                    email = email,
                    name = nickname,
                    profileImageUrl = profileImageUrl
                )
            )
        return CustomUserDetails(user)
    }
}