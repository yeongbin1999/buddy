package com.buddy.group.entity

import com.buddy.global.entity.BaseEntity
import com.buddy.interest.InterestType
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
class UserGroup(

    @Column(nullable = false, length = 100)
    var name: String,

    @Column(nullable = false, length = 255)
    var description: String,

    @Column(nullable = false, length = 50)
    var location: String,

    @Column(nullable = false)
    var maxMembers: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    var createdBy: User,

    // 모임은 관심사 하나만 지정
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    var interest: InterestType

) : BaseEntity()