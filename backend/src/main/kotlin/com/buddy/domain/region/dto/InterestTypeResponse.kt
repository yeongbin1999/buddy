package com.buddy.domain.region.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "관심사 타입 응답 DTO")
data class InterestTypeResponse(
    @Schema(description = "관심사 타입 코드", example = "STUDY")
    val name: String,
    @Schema(description = "관심사 타입 설명", example = "스터디/학습")
    val description: String
)
