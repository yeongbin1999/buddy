package com.buddy.domain.post.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class PostResponse(
    @Schema(description = "게시글 ID", example = "1")
    val id: Long,

    @Schema(description = "그룹 ID", example = "1")
    val groupId: Long,

    @Schema(description = "작성자 ID", example = "1")
    val authorId: Long,

    @Schema(description = "작성자 이름", example = "홍길동")
    val authorName: String?,

    @Schema(description = "게시글 제목", example = "우리 그룹 첫 게시글입니다!")
    val title: String?,

    @Schema(description = "게시글 내용", example = "안녕하세요, 그룹원 여러분! 잘 부탁드립니다.")
    val content: String,

    @Schema(description = "생성일", example = "2023-01-01T12:00:00")
    val createdAt: LocalDateTime,

    @Schema(description = "수정일", example = "2023-01-01T12:00:00")
    val updatedAt: LocalDateTime
)
