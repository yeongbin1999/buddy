package com.buddy.domain.photo.service

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.buddy.global.config.AwsS3Properties
import org.springframework.stereotype.Service
import java.net.URL
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date

@Service
class S3Service(
    private val amazonS3: AmazonS3,
    private val awsS3Properties: AwsS3Properties
) {

    fun generatePresignedUrl(objectKey: String, httpMethod: HttpMethod): URL {
        val expiration = Date.from(Instant.now().plus(5, ChronoUnit.MINUTES)) // 5분 유효

        val generatePresignedUrlRequest = GeneratePresignedUrlRequest(awsS3Properties.s3.bucket, objectKey)
            .withMethod(httpMethod)
            .withExpiration(expiration)

        return amazonS3.generatePresignedUrl(generatePresignedUrlRequest)
    }
}
