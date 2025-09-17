package com.buddy.domain.region.dto

data class RegionMunicipalityResponse(
    val id: Long,
    val name: String,
    val provinceId: Long,
    val provinceName: String
)
