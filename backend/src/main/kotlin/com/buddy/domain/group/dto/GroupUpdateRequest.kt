package com.buddy.domain.group.dto

data class GroupUpdateRequest(
    val title: String,
    val description: String,
    val imageUrl: String,
    val interest: String,
    val regionId: Long
)
