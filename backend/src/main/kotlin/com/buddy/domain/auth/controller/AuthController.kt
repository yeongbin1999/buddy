package com.buddy.domain.auth.controller

import com.buddy.domain.auth.dto.OAuthCallbackRequest
import com.buddy.domain.auth.service.AuthService
import com.buddy.domain.auth.service.OAuthService
import com.buddy.global.dto.RsData
import com.buddy.global.extension.toSuccessResponseWithoutData
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
    private val oauthService: OAuthService
) {
    @PostMapping("/logout")
    fun logout(
        @RequestHeader("Authorization") authorization: String,
        @RequestHeader("X-Device-Id", required = false) deviceIdHeader: String?,
        response: HttpServletResponse
    ): ResponseEntity<RsData<Nothing>> {
        val deviceId = getOrGenerateDeviceId(deviceIdHeader, response)
        val accessToken = authorization.removePrefix("Bearer ")
        authService.logout(accessToken, deviceId)
        deleteRefreshTokenCookie(response)
        return toSuccessResponseWithoutData("로그아웃 성공")
    }

    @PostMapping("/reissue")
    fun reissue(
        @CookieValue("refreshToken") refreshToken: String,
        @RequestHeader("X-Device-Id", required = false) deviceIdHeader: String?,
        response: HttpServletResponse
    ): ResponseEntity<RsData<Nothing>> {
        val deviceId = getOrGenerateDeviceId(deviceIdHeader, response)
        val newTokens = authService.reissue(refreshToken, deviceId)
        setRefreshTokenCookie(response, newTokens.refreshToken, newTokens.refreshTokenExpiration.seconds)
        setAccessTokenHeader(response, newTokens.accessToken)
        return toSuccessResponseWithoutData("토큰 재발급 성공")
    }

    @PostMapping("/oauth/callback/{provider}")
    suspend fun oauthCallback(
        @PathVariable provider: String,
        @RequestBody request: OAuthCallbackRequest,
        @RequestHeader("X-Device-Id", required = false) deviceIdHeader: String?,
        response: HttpServletResponse
    ): ResponseEntity<RsData<Nothing>> {
        val deviceId = getOrGenerateDeviceId(deviceIdHeader, response)
        val tokenDto = oauthService.handleOAuthCallback(provider, request, deviceId)
        setRefreshTokenCookie(response, tokenDto.refreshToken, tokenDto.refreshTokenExpiration.seconds)
        setAccessTokenHeader(response, tokenDto.accessToken)
        return toSuccessResponseWithoutData("OAuth 로그인 성공")
    }

    private fun getOrGenerateDeviceId(deviceIdHeader: String?, response: HttpServletResponse): String {
        if (deviceIdHeader.isNullOrBlank()) {
            val newDeviceId = UUID.randomUUID().toString()
            response.addHeader("X-Device-Id", newDeviceId)
            response.addHeader("X-Device-Id-Source", "generated")
            return newDeviceId
        }
        return deviceIdHeader
    }

    private fun setAccessTokenHeader(response: HttpServletResponse, token: String) {
        response.addHeader("Authorization", "Bearer $token")
    }

    private fun setRefreshTokenCookie(response: HttpServletResponse, token: String, maxAge: Long) {
        val cookie = ResponseCookie.from("refreshToken", token)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(maxAge)
            .sameSite("Strict")
            .build()
        response.addHeader("Set-Cookie", cookie.toString())
    }

    private fun deleteRefreshTokenCookie(response: HttpServletResponse) {
        val cookie = ResponseCookie.from("refreshToken", "")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(0)
            .sameSite("Strict")
            .build()
        response.addHeader("Set-Cookie", cookie.toString())
    }
}