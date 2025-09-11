package com.buddy.user.repository

import com.buddy.user.entity.UserProvider
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserProviderRepository : JpaRepository<UserProvider, Long>