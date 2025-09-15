package com.buddy.global.exception

open class CustomException(
    val errorCode: ErrorCode
) : RuntimeException(errorCode.message)