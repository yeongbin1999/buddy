package com.buddy.domain.user.service

import com.buddy.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    val userRepository: UserRepository
) {
    fun findById(id: Int) = userRepository.findById(id.toLong()).orElseThrow()
}