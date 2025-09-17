package com.buddy.domain.post.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class CommentResponse(
    @Schema(description = "댓글 ID", example = "1")
    val id: Long,

    @Schema(description = "게시글 ID", example = "1")
    val postId: Long,

    @Schema(description = "작성자 ID", example = "1")
    val authorId: Long,

    @Schema(description = "작성자 이름", example = "홍길동")
    val authorName: String?,

    @Schema(description = "부모 댓글 ID (대댓글인 경우)", example = "1")
    val parentId: Long?,

    @Schema(description = "댓글 내용", example = "첫 댓글입니다!")
    val content: String,

    @Schema(description = "생성일", example = "2023-01-01T12:00:00")
    val createdAt: LocalDateTime,

    @Schema(description = "수정일", example = "2023-01-01T12:00:00")
    val updatedAt: LocalDateTime,

    @Schema(description = "자식 댓글 목록")
    val children: List<CommentResponse> = emptyList()
)
