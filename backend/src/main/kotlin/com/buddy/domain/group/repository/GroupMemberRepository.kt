package com.buddy.domain.group.repository

import com.buddy.domain.group.entity.Group
import com.buddy.domain.group.entity.GroupMember
import com.buddy.domain.user.entity.User
import com.buddy.enum.GroupMemberStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface GroupMemberRepository : JpaRepository<GroupMember, Long> {
    fun existsByUserAndGroup(user: User, group: Group): Boolean
    fun findByUserAndGroup(user: User, group: Group): GroupMember?
    fun findByUserAndGroupAndStatus(user: User, group: Group, status: GroupMemberStatus): Optional<GroupMember>
}
