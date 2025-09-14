package com.buddy.security.service

import com.buddy.domain.user.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(user: User) : UserDetails {

    val id: Int = user.id
    val email: String = user.email
    val nickname: String = user.nickname
    val role: Role = user.role
    private val _authorities: List<GrantedAuthority> = listOf(SimpleGrantedAuthority(user.role.roleName))

    override fun getAuthorities(): Collection<GrantedAuthority> = _authorities
    override fun getUsername(): String = email
    override fun getPassword(): String? = null
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
}


