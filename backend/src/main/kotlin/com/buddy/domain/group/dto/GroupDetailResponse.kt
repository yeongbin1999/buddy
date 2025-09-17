package com.buddy.domain.group.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class GroupDetailResponse(
    @Schema(description = "그룹 ID", example = "1")
    val id: Long,

    @Schema(description = "그룹명", example = "우리 동네 독서 모임")
    val title: String,

    @Schema(description = "그룹 설명", example = "매주 주말에 모여 책을 읽고 토론합니다.")
    val description: String,

    @Schema(description = "그룹 대표 이미지 URL", example = "https://example.com/group_image.jpg")
    val imageUrl: String,

    @Schema(description = "그룹 관심사", example = "STUDY")
    val interest: String,

    @Schema(description = "지역명", example = "강남구")
    val region: String,

    @Schema(description = "최소 인원수", example = "2")
    val minMemberCount: Int,

    @Schema(description = "최대 인원수", example = "10")
    val maxMemberCount: Int,

    @Schema(description = "멤버 목록")
    val members: List<GroupMemberResponse>,

    @Schema(description = "생성일", example = "2023-01-01T12:00:00")
    val createdAt: LocalDateTime
)
