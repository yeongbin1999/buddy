package com.buddy.domain.group.entity

import com.buddy.common.GroupMemberStatus
import com.buddy.common.GroupRole
import com.buddy.global.entity.BaseEntity
import com.buddy.domain.user.entity.User
import jakarta.persistence.*

@Entity
@Table(
    name = "group_member",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_group_member_group_user",
            columnNames = ["group_id", "user_id"]
        )
    ],
    indexes = [
        Index(name = "idx_group_member_group", columnList = "group_id"),
        Index(name = "idx_group_member_user", columnList = "user_id"),
        Index(name = "idx_group_member_status", columnList = "status")
    ]
)
open class GroupMember : BaseEntity() {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "group_id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_member_group")
    )
    lateinit var group: Group

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_member_user")
    )
    lateinit var user: User

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    var role: GroupRole = GroupRole.MEMBER

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: GroupMemberStatus = GroupMemberStatus.APPLIED
}