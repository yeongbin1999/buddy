package com.buddy.global.extension

import com.buddy.global.dto.RsData
import com.buddy.global.exception.ErrorCode
import org.springframework.http.ResponseEntity

// 성공 응답 확장
fun <T> T.toSuccessResponse(msg: String = "성공"): ResponseEntity<RsData<T>> =
    ResponseEntity.ok(RsData.success(msg, this))

// 실패 응답 확장
fun ErrorCode.toFailResponse(customMsg: String? = null): ResponseEntity<RsData<Nothing>> =
    ResponseEntity.status(this.status)
        .body(RsData.fail(this, customMsg))

fun toSuccessResponseWithoutData(msg: String = "성공"): ResponseEntity<RsData<Unit>> =
    ResponseEntity.ok(RsData.success(msg))