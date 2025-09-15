package com.buddy.photo.entity

import com.buddy.global.entity.BaseEntity
import com.buddy.domain.group.entity.Group
import com.buddy.domain.user.entity.User
import jakarta.persistence.*

@Entity
@Table(
    name = "group_photo",
    indexes = [
        Index(name = "idx_group_photo_group_timeline", columnList = "group_id, id")
    ]
)
open class GroupPhoto : BaseEntity() {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "group_id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_photo_group")
    )
    lateinit var group: Group

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "uploader_id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_photo_uploader")
    )
    lateinit var uploader: User

    @Column(name = "image_url", nullable = false, length = 512)
    lateinit var imageUrl: String

    @Column(name = "caption", length = 200)
    var caption: String? = null

    @Column(name = "is_deleted", nullable = false)
    var isDeleted: Boolean = false
}