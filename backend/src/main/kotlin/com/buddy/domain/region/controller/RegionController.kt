package com.buddy.domain.region.controller

import com.buddy.common.RsData
import com.buddy.domain.region.dto.InterestTypeResponse
import com.buddy.domain.region.dto.RegionMunicipalityResponse
import com.buddy.domain.region.dto.RegionProvinceResponse
import com.buddy.domain.region.service.RegionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/regions")
class RegionController(
    private val regionService: RegionService
) {

    @GetMapping("/provinces")
    fun getAllProvinces(): RsData<List<RegionProvinceResponse>> {
        val provinces = regionService.getAllProvinces()
        return RsData.success(data = provinces)
    }

    @GetMapping("/provinces/{provinceId}/municipalities")
    fun getMunicipalitiesByProvinceId(@PathVariable provinceId: Long): RsData<List<RegionMunicipalityResponse>> {
        val municipalities = regionService.getMunicipalitiesByProvinceId(provinceId)
        return RsData.success(data = municipalities)
    }

    @GetMapping("/interest-types")
    fun getAllInterestTypes(): RsData<List<InterestTypeResponse>> {
        val interestTypes = regionService.getAllInterestTypes()
        return RsData.success(data = interestTypes)
    }
}
