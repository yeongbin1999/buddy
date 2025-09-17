package com.buddy.domain.photo.controller

import com.amazonaws.HttpMethod
import com.buddy.common.Rq
import com.buddy.domain.photo.service.S3Service
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Photo Upload", description = "사진 업로드 관련 API (S3 사전 서명된 URL)")
@RestController
@RequestMapping("/api/v1/photos")
@SecurityRequirement(name = "bearerAuth")
class PhotoUploadController(
    private val s3Service: S3Service,
    private val rq: Rq
) {

    @Operation(summary = "S3 사전 서명된 URL 요청", description = "클라이언트가 S3에 직접 파일을 업로드할 수 있는 사전 서명된 URL을 요청합니다.")
    @ApiResponse(responseCode = "200", description = "사전 서명된 URL 생성 성공")
    @Parameter(name = "fileName", description = "업로드할 파일 이름 (확장자 포함)", required = true, example = "my-image.jpg")
    @Parameter(name = "contentType", description = "파일의 Content-Type (예: image/jpeg)", required = true, example = "image/jpeg")
    @GetMapping("/presigned-url")
    fun getPresignedUrl(
        @RequestParam fileName: String,
        @RequestParam contentType: String
    ): ResponseEntity<String> {
        // TODO: 파일 이름 및 Content-Type 유효성 검사
        // TODO: 사용자 인증 및 권한 확인 (예: 특정 그룹에 속한 사용자만 업로드 가능하도록)

        val objectKey = "uploads/${rq.getUserId()}/${System.currentTimeMillis()}_$fileName"
        val presignedUrl = s3Service.generatePresignedUrl(objectKey, HttpMethod.PUT)
        return ResponseEntity.ok(presignedUrl.toString())
    }
}
