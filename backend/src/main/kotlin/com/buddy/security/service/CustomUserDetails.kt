package com.buddy.security.service

import com.buddy.domain.user.entity.User
import com.buddy.enum.UserRole
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(user: User) : UserDetails {

    val id: Long? = user.id
    val name: String? = user.name
    val role: UserRole = user.role
    private val authorities: List<GrantedAuthority> = listOf(SimpleGrantedAuthority(user.role.roleName))

    override fun getAuthorities(): Collection<GrantedAuthority> = authorities
    override fun getUsername(): String = id.toString()
    override fun getPassword(): String? = null
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
}


