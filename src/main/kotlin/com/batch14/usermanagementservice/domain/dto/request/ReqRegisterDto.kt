package com.batch14.usermanagementservice.domain.dto.request

import jakarta.validation.constraints.NotBlank

data class ReqRegisterDto(
    @field:NotBlank(message = "username cannot be blank")
    val username: String,
    @field:NotBlank(message = "user email cannot be blank")
    val email: String,

    val password: String,
    val roleId: Int? = null,
)