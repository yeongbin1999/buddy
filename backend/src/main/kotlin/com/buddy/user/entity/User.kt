package com.buddy.user.entity

import com.buddy.common.AuthProvider
import com.buddy.common.InterestType
import com.buddy.common.UserStatus
import com.buddy.global.entity.BaseEntity
import com.buddy.region.entity.RegionMunicipality
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(
    name = "users",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uq_users_provider_uid",
            columnNames = ["provider", "provider_user_id"]
        )
    ],
    indexes = [
        Index(name = "idx_users_municipality", columnList = "municipality_id"),
        Index(name = "idx_users_status", columnList = "status")
    ]
)
open class User : BaseEntity() {

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 20)
    lateinit var provider: AuthProvider

    @Column(name = "provider_user_id", nullable = false, length = 100)
    lateinit var providerId: String

    /** 온보딩 이전엔 null 허용, ACTIVE 전환 시 필수 */
    @Column(name = "name", length = 50)
    var name: String? = null

    /** 온보딩 이전엔 null 허용, ACTIVE 전환 시 필수 */
    @Column(name = "birthdate")
    var birthdate: LocalDate? = null

    /** 온보딩 이전엔 null 허용, ACTIVE 전환 시 필수 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "municipality_id",
        foreignKey = ForeignKey(name = "fk_users_municipality")
    )
    var municipality: RegionMunicipality? = null

    /** 관심사: (user_id, interest) 유니크 + LAZY */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "user_interest",
        joinColumns = [JoinColumn(name = "user_id")],
        uniqueConstraints = [
            UniqueConstraint(
                name = "uk_user_interest",
                columnNames = ["user_id", "interest"]
            )
        ]
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "interest", length = 50, nullable = false)
    var interests: MutableSet<InterestType> = mutableSetOf()

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: UserStatus = UserStatus.INCOMPLETE
}
