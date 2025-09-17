package com.buddy.global.extension

import com.buddy.common.RsData
import com.buddy.global.exception.ErrorCode
import org.springframework.http.ResponseEntity

// 성공 응답 확장
fun <T> T.toSuccessResponse(msg: String = "성공"): ResponseEntity<RsData<T>> =
    ResponseEntity.ok(RsData.success(msg, this))

fun toSuccessResponseWithoutData(msg: String = "성공"): ResponseEntity<RsData<Nothing>> =
    ResponseEntity.ok(RsData.success(msg))

// 실패 응답 확장
fun ErrorCode.toFailResponse(customMsg: String? = null): ResponseEntity<RsData<Nothing>> =
    ResponseEntity.status(this.status)
        .body(RsData.fail(this, customMsg))