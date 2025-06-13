package com.batch14.usermanagementservice.service

import com.batch14.usermanagementservice.domain.dto.request.ReqLoginDto
import com.batch14.usermanagementservice.domain.dto.request.ReqRegisterDto
import com.batch14.usermanagementservice.domain.dto.response.ResGetUserDto
import com.batch14.usermanagementservice.domain.dto.response.ResLoginDto


//todo: get user by id
interface MasterUserService {
    fun findAllActiveUsers(): List<ResGetUserDto>
    fun findUserById(id: Int): ResGetUserDto?
    fun register(req: ReqRegisterDto): ResGetUserDto
    fun login(req: ReqLoginDto): ResLoginDto
}