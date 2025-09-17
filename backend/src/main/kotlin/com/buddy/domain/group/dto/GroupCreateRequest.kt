package com.buddy.domain.group.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class GroupCreateRequest(
    @field:NotBlank(message = "그룹명은 필수입니다.")
    @field:Size(min = 2, max = 60, message = "그룹명은 2자 이상 60자 이하로 입력해주세요.")
    @Schema(description = "그룹명", example = "우리 동네 독서 모임")
    val title: String,

    @field:NotBlank(message = "그룹 설명은 필수입니다.")
    @field:Size(min = 10, max = 500, message = "그룹 설명은 10자 이상 500자 이하로 입력해주세요.")
    @Schema(description = "그룹 설명", example = "매주 주말에 모여 책을 읽고 토론합니다.")
    val description: String,

    @field:NotBlank(message = "이미지 URL은 필수입니다.")
    @field:Size(max = 512, message = "이미지 URL은 512자를 초과할 수 없습니다.")
    @Schema(description = "그룹 대표 이미지 URL", example = "https://example.com/group_image.jpg")
    val imageUrl: String,

    @field:NotBlank(message = "관심사는 필수입니다.")
    @Schema(description = "그룹 관심사 (enum)", example = "STUDY")
    val interest: String,

    @Schema(description = "지역 ID", example = "1")
    val regionId: Long,

    @field:Min(value = 2, message = "최소 인원수는 2명 이상이어야 합니다.")
    @field:Max(value = 30, message = "최소 인원수는 30명 이하이어야 합니다.")
    @Schema(description = "최소 인원수", example = "2")
    val minMemberCount: Int = 2,

    @field:Min(value = 2, message = "최대 인원수는 2명 이상이어야 합니다.")
    @field:Max(value = 30, message = "최대 인원수는 30명 이하이어야 합니다.")
    @Schema(description = "최대 인원수", example = "10")
    val maxMemberCount: Int = 30
)
