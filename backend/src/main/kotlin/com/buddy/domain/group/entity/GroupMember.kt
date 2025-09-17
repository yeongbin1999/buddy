package com.buddy.domain.group.entity

import com.buddy.domain.user.entity.User
import com.buddy.enum.GroupMemberStatus
import com.buddy.enum.GroupRole
import com.buddy.global.entity.BaseEntity
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
class GroupMember(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false, foreignKey = ForeignKey(name = "fk_member_group"))
    var group: Group,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_member_user")
    )
    var user: User,

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    var role: GroupRole = GroupRole.MEMBER,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: GroupMemberStatus = GroupMemberStatus.APPLIED
) : BaseEntity() {

}