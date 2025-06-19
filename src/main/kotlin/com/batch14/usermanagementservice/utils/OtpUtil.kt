package com.batch14.usermanagementservice.utils

import org.springframework.stereotype.Component


@Component
class OtpUtil {
    fun generateOtp(): String {
        return (100000..999999).random().toString()
    }
}