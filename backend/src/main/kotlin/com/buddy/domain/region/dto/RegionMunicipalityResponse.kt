package com.buddy.domain.region.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "시/군/구 응답 DTO")
data class RegionMunicipalityResponse(
    @Schema(description = "시/군/구 ID", example = "101")
    val id: Long,
    @Schema(description = "시/군/구 이름", example = "강남구")
    val name: String,
    @Schema(description = "상위 시/도 ID", example = "1")
    val provinceId: Long,
    @Schema(description = "상위 시/도 이름", example = "서울특별시")
    val provinceName: String
)
