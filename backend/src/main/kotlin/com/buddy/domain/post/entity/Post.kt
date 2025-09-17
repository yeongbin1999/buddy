package com.buddy.board.entity

import com.buddy.domain.group.entity.Group
import com.buddy.domain.user.entity.User
import com.buddy.global.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "board_post",
    indexes = [
        // 타임라인 페이징: group별 id 역순
        Index(name = "idx_board_post_group_timeline", columnList = "group_id, id")
    ]
)
open class Post : BaseEntity() {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "group_id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_post_group")
    )
    lateinit var group: Group

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "author_id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_post_author")
    )
    lateinit var author: User

    @Column(name = "title", length = 120)
    var title: String? = null

    @Lob
    @Column(name = "content", nullable = false)
    lateinit var content: String

    @Column(name = "is_deleted", nullable = false)
    var isDeleted: Boolean = false
}