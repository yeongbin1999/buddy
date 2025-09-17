package com.buddy.board.entity

import com.buddy.domain.user.entity.User
import com.buddy.global.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "board_comment",
    indexes = [
        Index(name = "idx_board_comment_post_timeline", columnList = "post_id, id")
    ]
)
open class Comment : BaseEntity() {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "post_id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_comment_post")
    )
    lateinit var post: Post

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "author_id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_comment_author")
    )
    lateinit var author: User

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", foreignKey = ForeignKey(name = "fk_comment_parent"))
    var parent: Comment? = null

    @Column(name = "content", nullable = false, length = 1000)
    lateinit var content: String

    @Column(name = "is_deleted", nullable = false)
    var isDeleted: Boolean = false
}
