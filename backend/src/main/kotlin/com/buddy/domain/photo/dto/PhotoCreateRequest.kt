package com.buddy.domain.photo.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class PhotoCreateRequest(
    @field:NotBlank(message = "이미지 URL은 필수입니다.")
    @field:Size(max = 512, message = "이미지 URL은 512자를 초과할 수 없습니다.")
    @Schema(description = "이미지 URL", example = "https://example.com/image.jpg")
    val imageUrl: String,

    @field:Size(max = 200, message = "캡션은 200자를 초과할 수 없습니다.")
    @Schema(description = "사진 캡션", example = "즐거운 모임 사진")
    val caption: String? = null
)
