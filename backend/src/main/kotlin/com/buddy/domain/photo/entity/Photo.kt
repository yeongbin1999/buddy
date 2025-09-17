package com.buddy.domain.photo.entity

import com.buddy.domain.group.entity.Group
import com.buddy.domain.user.entity.User
import com.buddy.global.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "group_photo",
    indexes = [
        Index(name = "idx_group_photo_group_timeline", columnList = "group_id, id")
    ]
)
open class Photo(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "group_id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_photo_group")
    )
    var group: Group,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "uploader_id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_photo_uploader")
    )
    var uploader: User,

    @Column(name = "image_url", nullable = false, length = 512)
    var imageUrl: String,

    @Column(name = "caption", length = 200)
    var caption: String? = null,

    @Column(name = "is_deleted", nullable = false)
    var isDeleted: Boolean = false
) : BaseEntity() {

}