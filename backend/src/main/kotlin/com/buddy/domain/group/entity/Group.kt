package com.buddy.domain.group.entity

import com.buddy.domain.chat.entity.ChatRoom
import com.buddy.domain.region.entity.RegionMunicipality
import com.buddy.domain.user.entity.User
import com.buddy.enum.GroupMemberStatus
import com.buddy.enum.InterestType
import com.buddy.global.entity.BaseEntity
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
class Group(
    @Column(name = "title", nullable = false, length = 60)
    var title: String,

    @Column(name = "description", nullable = false, length = 500)
    var description: String,

    @Column(name = "image_url", nullable = false, length = 512)
    var imageUrl: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "interest", nullable = false, length = 30)
    var interest: InterestType,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "municipality_id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_groups_municipality")
    )
    var municipality: RegionMunicipality,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "owner_id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_groups_owner")
    )
    var owner: User,

    @Column(name = "min_member_count", nullable = false)
    var minMemberCount: Int,

    @Column(name = "max_member_count", nullable = false)
    var maxMemberCount: Int
) : BaseEntity() {

    @OneToMany(mappedBy = "group", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var members: MutableList<GroupMember> = mutableListOf()

    @OneToOne(mappedBy = "group", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var chatRoom: ChatRoom? = null

    /** 승인된 멤버 수 반환 */
    fun getApprovedMemberCount(): Int {
        return members.count { it.status == GroupMemberStatus.APPROVED }
    }
}