package com.buddy.region.entity

import com.buddy.global.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "region_province",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_province_official_code",
            columnNames = ["official_code"]
        )
    ]
)
open class RegionProvince(
    @Column(name = "official_code", nullable = false, unique = true, length = 2)
    val officialCode: String,

    @Column(name = "name", nullable = false, length = 30)
    var name: String
) : BaseEntity()