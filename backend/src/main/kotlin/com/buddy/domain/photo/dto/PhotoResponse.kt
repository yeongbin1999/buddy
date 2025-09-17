package com.buddy.domain.photo.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class PhotoResponse(
    @Schema(description = "사진 ID", example = "1")
    val id: Long,

    @Schema(description = "그룹 ID", example = "1")
    val groupId: Long,

    @Schema(description = "업로더 ID", example = "1")
    val uploaderId: Long,

    @Schema(description = "업로더 이름", example = "홍길동")
    val uploaderName: String?,

    @Schema(description = "이미지 URL", example = "https://example.com/image.jpg")
    val imageUrl: String,

    @Schema(description = "사진 캡션", example = "즐거운 모임 사진")
    val caption: String?,

    @Schema(description = "생성일", example = "2023-01-01T12:00:00")
    val createdAt: LocalDateTime
)
