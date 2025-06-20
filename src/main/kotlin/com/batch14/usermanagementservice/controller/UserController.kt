package com.batch14.usermanagementservice.controller

import com.batch14.usermanagementservice.domain.Constant
import com.batch14.usermanagementservice.domain.dto.request.ReqLoginDto
import com.batch14.usermanagementservice.domain.dto.request.ReqRegisterDto
import com.batch14.usermanagementservice.domain.dto.request.ReqUpdateUserDto
import com.batch14.usermanagementservice.domain.dto.request.ReqVerifyOtpDto
import com.batch14.usermanagementservice.domain.dto.response.BaseResponse
import com.batch14.usermanagementservice.domain.dto.response.ResGetUserDto
import com.batch14.usermanagementservice.domain.dto.response.ResLoginDto
import com.batch14.usermanagementservice.domain.dto.response.ResRegisterDto
import com.batch14.usermanagementservice.service.MasterUserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/users")
class UserController(
    private val masterUserService: MasterUserService,
    private val httpServletRequest: HttpServletRequest

) {
    @GetMapping("/active")
    fun getAllActiveUsers(): ResponseEntity<BaseResponse<List<ResGetUserDto>>> {
        return ResponseEntity.ok(
            BaseResponse(
                data = masterUserService.findAllActiveUsers()
            )
        )
    }

    @GetMapping("/{id}")
    fun getUserById(
        @PathVariable id: Int
    ): ResponseEntity<BaseResponse<ResGetUserDto>> {
        return ResponseEntity.ok(
            BaseResponse(
                data = masterUserService.findUserById(id)
            )
        )
    }

    @PostMapping("/register")
    fun registerUser(
        @Valid @RequestBody req: ReqRegisterDto
    ): ResponseEntity<BaseResponse<ResRegisterDto>> {
        return ResponseEntity(
            BaseResponse(
                data = masterUserService.register(req)
            ),
            HttpStatus.CREATED
        )
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody req: ReqLoginDto
    ): ResponseEntity<BaseResponse<ResLoginDto>> {
        return ResponseEntity(
            BaseResponse(
                data = masterUserService.login(req),
                message = "Login successful"
            ),
            HttpStatus.OK
        )
    }

    @PostMapping("/verify-otp")
    fun verifyOtp(
        @RequestBody req: ReqVerifyOtpDto
    ): ResponseEntity<BaseResponse<String>> {
        masterUserService.verifyOtp(req)
        return ResponseEntity(
            BaseResponse(
                message = "OTP verified successfully"
            ),
            HttpStatus.OK
        )
    }

    @PutMapping()
    fun updateUser(
        @RequestBody req: ReqUpdateUserDto
    ): ResponseEntity<BaseResponse<ResGetUserDto>> {
        val userId = httpServletRequest.getHeader(Constant.HEADER_USER_ID)
        return ResponseEntity(
            BaseResponse(
                data = masterUserService.updateUser(req, userId.toInt()),
            ),
            HttpStatus.OK
        )
    }

    @DeleteMapping("/{id}/hard-delete")
    fun hardDeleteUser(
        @PathVariable id: Int
    ): ResponseEntity<BaseResponse<String>> {
        val performerId = httpServletRequest.getHeader(Constant.HEADER_USER_ID).toInt()
        masterUserService.hardDeleteUser(id, performerId)
        return ResponseEntity(
            BaseResponse(
                message = "User deleted successfully"
            ),
            HttpStatus.OK
        )
    }

    @DeleteMapping("/{id}/soft-delete")
    fun softDeleteUser(
        @PathVariable id: Int
    ): ResponseEntity<BaseResponse<String>> {
        val performerId = httpServletRequest.getHeader(Constant.HEADER_USER_ID).toInt()
        masterUserService.softDeleteUser(id, performerId)
        return ResponseEntity(
            BaseResponse(
                message = "User soft deleted successfully"
            ),
            HttpStatus.OK
        )
    }
}