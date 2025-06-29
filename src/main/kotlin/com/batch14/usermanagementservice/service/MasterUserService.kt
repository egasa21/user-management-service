package com.batch14.usermanagementservice.service

import com.batch14.usermanagementservice.domain.dto.request.ReqLoginDto
import com.batch14.usermanagementservice.domain.dto.request.ReqRegisterDto
import com.batch14.usermanagementservice.domain.dto.request.ReqUpdateUserDto
import com.batch14.usermanagementservice.domain.dto.request.ReqVerifyOtpDto
import com.batch14.usermanagementservice.domain.dto.response.ResGetUserDto
import com.batch14.usermanagementservice.domain.dto.response.ResLoginDto
import com.batch14.usermanagementservice.domain.dto.response.ResRegisterDto


interface MasterUserService {
    fun findAllActiveUsers(): List<ResGetUserDto>
    fun findUserById(id: Int): ResGetUserDto?
    fun findUserByIds(ids: List<Int>): List<ResGetUserDto>
    fun register(req: ReqRegisterDto): ResRegisterDto
    fun login(req: ReqLoginDto): ResLoginDto
    fun updateUser(req: ReqUpdateUserDto, userId: Int): ResGetUserDto
    fun hardDeleteUser(userId: Int, performerId: Int)
    fun softDeleteUser(userId: Int, performerId: Int)
    fun verifyOtp(req: ReqVerifyOtpDto)

}