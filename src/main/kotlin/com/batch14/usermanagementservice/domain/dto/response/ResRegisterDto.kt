package com.batch14.usermanagementservice.domain.dto.response

import java.io.Serializable

data class ResRegisterDto(
    val otp: String
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = -91569882613829456L
    }
}
