package com.buddy.user.entity

import com.buddy.global.entity.BaseEntity
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "users")
class User(

    @Column(nullable = true, length = 50)
    var name: String? = null,

    @Column(nullable = true)
    var birthdate: LocalDate? = null,

    @Column(nullable = true, length = 100)
    var address: String? = null,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "user_interest",
        joinColumns = [JoinColumn(name = "user_id")]
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "interest")
    var interests: MutableSet<InterestType> = mutableSetOf(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: UserStatus = UserStatus.INCOMPLETE

) : BaseEntity()