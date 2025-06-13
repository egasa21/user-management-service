package com.batch14.usermanagementservice.exception

import com.batch14.usermanagementservice.domain.dto.response.BaseResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        e: MethodArgumentNotValidException
    ): ResponseEntity<BaseResponse<Any?>> {
        val errors = mutableListOf<String?>()
        e.bindingResult.fieldErrors.forEach {
            errors.add(it.defaultMessage)
        }
        return ResponseEntity(
            BaseResponse(
                data = errors,
            ),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(
        ex: CustomException
    ): ResponseEntity<BaseResponse<Any?>> {
        return ResponseEntity(
            BaseResponse(
                message = ex.message,
                status = "T",
                error = ex.data,
            ),
            HttpStatus.valueOf(ex.statusCode)
        )
    }
}