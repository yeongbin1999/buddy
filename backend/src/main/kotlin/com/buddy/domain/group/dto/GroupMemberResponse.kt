package com.buddy.domain.group.dto

import com.buddy.enum.GroupRole

data class GroupMemberResponse(
    val userId: Long,
    val name: String?,
    val profileUrl: String?,
    val role: GroupRole
)
