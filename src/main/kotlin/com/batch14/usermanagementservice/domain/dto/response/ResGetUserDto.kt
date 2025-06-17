package com.batch14.usermanagementservice.domain.dto.response

import jakarta.validation.constraints.NotBlank
import java.io.Serializable

data class ResGetUserDto(
    val id: Int,
    val email: String,
    val username: String,
    var roleId: Int? = null,
    var roleName: String? = null,
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 5175812874022657010L
    }
}