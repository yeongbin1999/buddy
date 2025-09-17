package com.buddy.domain.post.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class PostCreateRequest(
    @field:NotBlank(message = "제목은 필수입니다.")
    @field:Size(min = 2, max = 120, message = "제목은 2자 이상 120자 이하로 입력해주세요.")
    @Schema(description = "게시글 제목", example = "우리 그룹 첫 게시글입니다!")
    val title: String,

    @field:NotBlank(message = "내용은 필수입니다.")
    @Schema(description = "게시글 내용", example = "안녕하세요, 그룹원 여러분! 잘 부탁드립니다.")
    val content: String
)
