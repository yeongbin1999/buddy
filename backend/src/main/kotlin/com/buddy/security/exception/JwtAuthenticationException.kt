package com.buddy.security.exception

import com.buddy.global.exception.ErrorCode
import org.springframework.security.core.AuthenticationException

class JwtAuthenticationException : AuthenticationException {
    val errorCode: ErrorCode

    constructor(errorCode: ErrorCode) : super(errorCode.message) {
        this.errorCode = errorCode
    }

    constructor(errorCode: ErrorCode, cause: Throwable) : super(errorCode.message, cause) {
        this.errorCode = errorCode
    }
}