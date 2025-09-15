package com.buddy.domain.region.repository

import com.buddy.domain.region.entity.RegionMunicipality
import org.springframework.data.jpa.repository.JpaRepository

interface RegionMunicipalityRepository : JpaRepository<RegionMunicipality, Long> {
    fun findByOfficialCode(officialCode: String): RegionMunicipality?

    fun existsByOfficialCode(officialCode: String): Boolean
}