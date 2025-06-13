package com.batch14.usermanagementservice.exception

class CustomException(
    exceptionMessage: String,
    val statusCode: Int,
    val data: Any? = null
) : RuntimeException(exceptionMessage)