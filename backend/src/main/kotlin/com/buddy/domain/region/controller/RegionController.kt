package com.buddy.domain.region.controller

import com.buddy.common.RsData
import com.buddy.domain.region.dto.InterestTypeResponse
import com.buddy.domain.region.dto.RegionMunicipalityResponse
import com.buddy.domain.region.dto.RegionProvinceResponse
import com.buddy.domain.region.service.RegionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Region & Interest API", description = "지역 및 관심사 관련 API")
@RestController
@RequestMapping("/api/v1/regions")
class RegionController(
    private val regionService: RegionService
) {

    @Operation(summary = "모든 시/도 목록 조회", description = "대한민국의 모든 시/도 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 시/도 목록을 조회했습니다.")
    @GetMapping("/provinces")
    fun getAllProvinces(): RsData<List<RegionProvinceResponse>> {
        val provinces = regionService.getAllProvinces()
        return RsData.success(data = provinces)
    }

    @Operation(summary = "특정 시/도의 시/군/구 목록 조회", description = "주어진 provinceId에 해당하는 시/도의 모든 시/군/구 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 시/군/구 목록을 조회했습니다.")
    @ApiResponse(responseCode = "404", description = "해당 provinceId를 가진 시/도를 찾을 수 없습니다.")
    @GetMapping("/provinces/{provinceId}/municipalities")
    fun getMunicipalitiesByProvinceId(@PathVariable provinceId: Long): RsData<List<RegionMunicipalityResponse>> {
        val municipalities = regionService.getMunicipalitiesByProvinceId(provinceId)
        return RsData.success(data = municipalities)
    }

    @Operation(summary = "모든 관심사 타입 목록 조회", description = "애플리케이션에서 사용되는 모든 관심사 타입 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 관심사 타입 목록을 조회했습니다.")
    @GetMapping("/interest-types")
    fun getAllInterestTypes(): RsData<List<InterestTypeResponse>> {
        val interestTypes = regionService.getAllInterestTypes()
        return RsData.success(data = interestTypes)
    }
}
