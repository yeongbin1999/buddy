package com.buddy.domain.region.repository

import com.buddy.domain.region.entity.RegionMunicipality
import com.buddy.domain.region.entity.RegionProvince
import org.springframework.data.jpa.repository.JpaRepository

interface RegionMunicipalityRepository : JpaRepository<RegionMunicipality, Long> {
    fun findByOfficialCode(officialCode: String): RegionMunicipality?

    fun existsByOfficialCode(officialCode: String): Boolean

    fun findByProvince(province: RegionProvince): List<RegionMunicipality>
}