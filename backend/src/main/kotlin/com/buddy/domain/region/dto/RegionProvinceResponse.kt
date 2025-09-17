package com.buddy.domain.region.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "시/도 응답 DTO")
data class RegionProvinceResponse(
    @Schema(description = "시/도 ID", example = "1")
    val id: Long,
    @Schema(description = "시/도 이름", example = "서울특별시")
    val name: String
)
