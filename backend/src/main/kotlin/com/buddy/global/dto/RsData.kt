package com.buddy.global.dto

import com.buddy.global.exception.ErrorCode

data class RsData<T>(
    val resultCode: String,
    val msg: String? = null,
    val data: T? = null
) {
    val isSuccess: Boolean
        get() = resultCode.startsWith("SUCCESS")

    val isFail: Boolean
        get() = !isSuccess

    companion object {
        fun <T> success(msg: String, data: T? = null) =
            RsData("SUCCESS_200", msg, data)

        fun success(msg: String): RsData<Unit> = success(msg, Unit)

        fun <T> fail(errorCode: ErrorCode, customMsg: String? = null) : RsData<T> =
            RsData(errorCode.code, customMsg ?: errorCode.message, null)

        fun <T> of(resultCode: String, msg: String, data: T? = null) =
            RsData(resultCode, msg, data)
    }
}