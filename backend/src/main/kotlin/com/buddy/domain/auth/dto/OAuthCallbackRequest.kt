package com.buddy.domain.auth.dto

data class OAuthCallbackRequest(
    val code: String,
    val codeVerifier: String,
    val redirectUri: String
)