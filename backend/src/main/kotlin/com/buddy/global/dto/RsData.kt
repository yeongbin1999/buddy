package com.buddy.global.dto

import com.buddy.global.exception.ErrorCode
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class RsData<out T>(
    val resultCode: String,
    val msg: String? = null,
    val data: T? = null
) {
    val isSuccess: Boolean get() = resultCode.startsWith("SUCCESS")
    val isFail: Boolean get() = !isSuccess

    companion object {
        // 데이터 있는 성공
        fun <T> success(msg: String = "성공", data: T): RsData<T> =
            RsData(resultCode = "SUCCESS", msg = msg, data = data)

        // 데이터 없는 성공
        fun success(msg: String = "성공"): RsData<Nothing> =
            RsData(resultCode = "SUCCESS", msg = msg, data = null)

        // 실패(전역 예외핸들러에서 주로 사용)
        fun fail(code: ErrorCode, customMsg: String? = null): RsData<Nothing> =
            RsData(resultCode = code.code, msg = customMsg ?: code.message, data = null)
    }
}