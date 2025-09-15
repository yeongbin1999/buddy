package com.buddy.domain.region.repository

import com.buddy.domain.region.entity.RegionProvince
import org.springframework.data.jpa.repository.JpaRepository

interface RegionProvinceRepository : JpaRepository<RegionProvince, Long> {
    fun findByOfficialCode(officialCode: String): RegionProvince?

    fun existsByOfficialCode(officialCode: String): Boolean
}