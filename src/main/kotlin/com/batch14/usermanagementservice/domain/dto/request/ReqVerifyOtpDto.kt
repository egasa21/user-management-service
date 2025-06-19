package com.batch14.usermanagementservice.domain.dto.request

data class ReqVerifyOtpDto(
    val email: String,
    val otp: String
)
