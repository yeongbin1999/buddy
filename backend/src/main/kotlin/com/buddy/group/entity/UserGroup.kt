package com.buddy.group.entity

import com.buddy.global.entity.BaseEntity
import com.buddy.user.entity.InterestType
import com.buddy.user.entity.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "user_groups")
class UserGroup : BaseEntity() {

    @Column(nullable = false, length = 100)
    lateinit var name: String

    @Column(nullable = false, length = 255)
    lateinit var description: String

    @Column(nullable = false, length = 50)
    lateinit var location: String

    @Column(nullable = false)
    var maxMembers: Int = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    lateinit var createdBy: User

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    lateinit var interest: InterestType
}