package com.buddy.domain.post.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class PostUpdateRequest(
    @field:NotBlank(message = "제목은 필수입니다.")
    @field:Size(min = 2, max = 120, message = "제목은 2자 이상 120자 이하로 입력해주세요.")
    @Schema(description = "게시글 제목", example = "수정된 게시글 제목입니다.")
    val title: String,

    @field:NotBlank(message = "내용은 필수입니다.")
    @Schema(description = "게시글 내용", example = "수정된 게시글 내용입니다.")
    val content: String
)
