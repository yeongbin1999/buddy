package com.buddy.domain.region.service

import com.buddy.domain.region.dto.InterestTypeResponse
import com.buddy.domain.region.dto.RegionMunicipalityResponse
import com.buddy.domain.region.dto.RegionProvinceResponse
import com.buddy.domain.region.repository.RegionMunicipalityRepository
import com.buddy.domain.region.repository.RegionProvinceRepository
import com.buddy.enum.InterestType
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RegionService(
    private val regionProvinceRepository: RegionProvinceRepository,
    private val regionMunicipalityRepository: RegionMunicipalityRepository
) {

    @Transactional(readOnly = true)
    fun getAllProvinces(): List<RegionProvinceResponse> {
        return regionProvinceRepository.findAll()
            .map { RegionProvinceResponse(it.id!!, it.name) }
    }

    @Transactional(readOnly = true)
    fun getMunicipalitiesByProvinceId(provinceId: Long): List<RegionMunicipalityResponse> {
        val province = regionProvinceRepository.findById(provinceId)
            .orElseThrow { EntityNotFoundException("Province not found with ID: $provinceId") }
        return regionMunicipalityRepository.findByProvince(province)
            .map { RegionMunicipalityResponse(it.id!!, it.name, it.province.id!!, it.province.name) }
    }

    fun getAllInterestTypes(): List<InterestTypeResponse> {
        return InterestType.entries.map { InterestTypeResponse(it.name, it.description) }
    }
}
