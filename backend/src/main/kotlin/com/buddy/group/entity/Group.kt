package com.buddy.group.entity

import com.buddy.common.InterestType
import com.buddy.global.entity.BaseEntity
import com.buddy.region.entity.RegionMunicipality
import com.buddy.user.entity.User
import jakarta.persistence.*

@Entity
@Table(
    name = "groups",
    indexes = [
        Index(name = "idx_groups_interest", columnList = "interest"),
        Index(name = "idx_groups_municipality", columnList = "municipality_id"),
        Index(name = "idx_groups_owner", columnList = "owner_id")
    ]
)
open class Group : BaseEntity() {

    @Column(name = "title", nullable = false, length = 60)
    lateinit var title: String

    @Column(name = "description", nullable = false, length = 500)
    lateinit var description: String

    @Enumerated(EnumType.STRING)
    @Column(name = "interest", nullable = false, length = 30)
    var interest: InterestType = InterestType.STUDY

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "municipality_id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_groups_municipality")
    )
    var municipality: RegionMunicipality? = null

    @Column(name = "image_url", nullable = false, length = 512)
    lateinit var imageUrl: String

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "owner_id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_groups_owner")
    )
    var owner: User? = null
}
