package com.buddy.region.entity

import com.buddy.global.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "region_municipality",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_muni_official_code",
            columnNames = ["official_code"]
        )
    ],
    indexes = [
        Index(name = "idx_muni_province_name", columnList = "province_id, name")
    ]
)
open class RegionMunicipality(
    @Column(name = "official_code", nullable = false, unique = true, length = 5)
    val officialCode: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "province_id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_muni_province")
    )
    val province: RegionProvince,

    @Column(name = "name", nullable = false, length = 40)
    var name: String,

    @Column(name = "short_name", length = 30)
    var shortName: String? = null
) : BaseEntity()