package com.buddy.domain.post.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CommentUpdateRequest(
    @field:NotBlank(message = "댓글 내용은 필수입니다.")
    @field:Size(max = 1000, message = "댓글 내용은 1000자를 초과할 수 없습니다.")
    @Schema(description = "댓글 내용", example = "수정된 댓글입니다.")
    val content: String
)
