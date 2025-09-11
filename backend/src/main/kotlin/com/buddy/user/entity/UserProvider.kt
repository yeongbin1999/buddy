package com.buddy.user.entity

import com.buddy.global.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "user_providers")
class UserProvider(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(nullable = false, length = 20)
    var provider: String, // "google", "naver", "kakao"

    @Column(nullable = false, length = 100)
    var providerUserId: String, // OAuth 플랫폼이 주는 고유 ID

    @Column(nullable = true, length = 100)
    var email: String? = null

) : BaseEntity()