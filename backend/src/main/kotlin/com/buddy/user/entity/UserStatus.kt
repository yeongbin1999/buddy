package com.buddy.user.entity

enum class UserStatus {
    INCOMPLETE,  // OAuth 로그인 직후, 프로필 미완성
    ACTIVE,      // 프로필 완료
    DELETED      // 탈퇴 처리
}
