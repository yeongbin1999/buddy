package com.buddy.domain.auth.controller

import com.buddy.common.RsData
import com.buddy.domain.auth.dto.OAuthCallbackRequest
import com.buddy.domain.auth.service.AuthService
import com.buddy.domain.auth.service.OAuthService
import com.buddy.global.extension.toSuccessResponseWithoutData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
    private val oauthService: OAuthService
) {
    @Operation(
        summary = "로그아웃",
        description = "현재 로그인된 사용자를 로그아웃 처리하고 리프레시 토큰 쿠키를 삭제합니다."
    )
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @PostMapping("/logout")
    fun logout(
        @Parameter(description = "액세스 토큰 (Bearer)") @RequestHeader("Authorization") authorization: String,
        @Parameter(description = "기기 ID (선택 사항, 없으면 새로 생성)") @RequestHeader("X-Device-Id", required = false) deviceIdHeader: String?,
        response: HttpServletResponse
    ): ResponseEntity<RsData<Nothing>> {
        val deviceId = getOrGenerateDeviceId(deviceIdHeader, response)
        val accessToken = authorization.removePrefix("Bearer ")
        authService.logout(accessToken, deviceId)
        deleteRefreshTokenCookie(response)
        return toSuccessResponseWithoutData("로그아웃 성공")
    }

    @Operation(
        summary = "토큰 재발급",
        description = "만료된 액세스 토큰을 리프레시 토큰을 사용하여 재발급합니다."
    )
    @ApiResponse(responseCode = "200", description = "토큰 재발급 성공")
    @ApiResponse(responseCode = "401", description = "리프레시 토큰 만료 또는 유효하지 않음")
    @PostMapping("/reissue")
    fun reissue(
        @Parameter(description = "리프레시 토큰 (쿠키)") @CookieValue("refreshToken") refreshToken: String,
        @Parameter(description = "기기 ID (선택 사항, 없으면 새로 생성)") @RequestHeader("X-Device-Id", required = false) deviceIdHeader: String?,
        response: HttpServletResponse
    ): ResponseEntity<RsData<Nothing>> {
        val deviceId = getOrGenerateDeviceId(deviceIdHeader, response)
        val newTokens = authService.reissue(refreshToken, deviceId)
        setRefreshTokenCookie(response, newTokens.refreshToken, newTokens.refreshTokenExpiration.seconds)
        setAccessTokenHeader(response, newTokens.accessToken)
        return toSuccessResponseWithoutData("토큰 재발급 성공")
    }

    @Operation(
        summary = "OAuth 콜백 처리",
        description = "OAuth 제공자로부터 인증 코드를 받아 토큰을 발급하고 로그인 처리합니다."
    )
    @ApiResponse(responseCode = "200", description = "OAuth 로그인 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 OAuth 인증 실패")
    @PostMapping("/oauth/callback/{provider}")
    suspend fun oauthCallback(
        @Parameter(description = "OAuth 제공자 (예: google, kakao, naver)") @PathVariable provider: String,
        @Parameter(description = "OAuth 콜백 요청 데이터") @RequestBody request: OAuthCallbackRequest,
        @Parameter(description = "기기 ID (없으면 새로 생성)") @RequestHeader("X-Device-Id", required = false) deviceIdHeader: String?,
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