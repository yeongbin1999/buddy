package com.buddy.domain.post.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CommentCreateRequest(
    @field:NotBlank(message = "댓글 내용은 필수입니다.")
    @field:Size(max = 1000, message = "댓글 내용은 1000자를 초과할 수 없습니다.")
    @Schema(description = "댓글 내용", example = "첫 댓글입니다!")
    val content: String,

    @Schema(description = "부모 댓글 ID (대댓글인 경우)", example = "1")
    val parentId: Long? = null
)
