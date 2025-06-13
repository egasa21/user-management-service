package com.batch14.usermanagementservice.service.impl

import com.batch14.usermanagementservice.domain.dto.request.ReqLoginDto
import com.batch14.usermanagementservice.domain.dto.request.ReqRegisterDto
import com.batch14.usermanagementservice.domain.dto.response.ResGetUserDto
import com.batch14.usermanagementservice.domain.dto.response.ResLoginDto
import com.batch14.usermanagementservice.domain.entity.MasterUserEntity
import com.batch14.usermanagementservice.exception.CustomException
import com.batch14.usermanagementservice.repository.MasterRoleRepository
import com.batch14.usermanagementservice.repository.MasterUserRepository
import com.batch14.usermanagementservice.service.MasterRoleService
import com.batch14.usermanagementservice.service.MasterUserService
import com.batch14.usermanagementservice.utils.BCryptUtil
import com.batch14.usermanagementservice.utils.JwtUtil
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class MasterUserServiceImpl(
    private val masterUserRepository: MasterUserRepository,
    private val masterRoleService: MasterRoleService,
    private val masterRoleRepository: MasterRoleRepository,
    private val bcrypt: BCryptUtil,
    private val jwtUtil: JwtUtil
) : MasterUserService {
    override fun findAllActiveUsers(): List<ResGetUserDto> {
        val rawData = masterUserRepository.findAllActiveRoles()
        val result = mutableListOf<ResGetUserDto>()
        rawData.forEach { u ->
            result.add(
                ResGetUserDto(
                    username = u.username,
                    id = u.id,
                    email = u.email,
//                    jika user memiliki role maka ambil id
//                    jika user tidak memiliki role make null
                    roleId = u.role?.id,
//                    jika user memiliki role maka ambil name
//                    jika user tidak memiliki role make role name null
                    roleName = u.role?.name,
                )
            )
        }
        return result
    }

    override fun findUserById(id: Int): ResGetUserDto? {
        val rawData = masterUserRepository.findById(id)
        return rawData.map { user ->
            ResGetUserDto(
                id = user.id,
                username = user.username,
                email = user.email,
                roleId = user.role?.id,
                roleName = user.role?.name
            )
        }.orElse(null)
    }

    override fun register(req: ReqRegisterDto): ResGetUserDto {

        val role = if (req.roleId == null) {
            Optional.empty()
        } else {
            masterRoleRepository.findById(req.roleId)
        }

        if (role.isEmpty && req.roleId != null) {
            throw CustomException(
                "Role not found",
                400
            )
        }


        val existingUser = masterUserRepository.findFirstByEmail(req.email)
        if (existingUser != null) {
            throw CustomException(
                "Email already registered",
                400
            )
        }

        if (masterUserRepository.findFirstByUsername(req.username).isPresent) {
            throw CustomException(
                "Username already taken",
                400
            )
        }


        val userRow = MasterUserEntity(
            email = req.email,
            password = req.password,
            username = req.username,
            role = if (role.isPresent) role.get() else null
        )
        val user = masterUserRepository.save(userRow)
        return ResGetUserDto(
            id = user.id,
            username = user.username,
            email = user.email,
            roleId = user.role?.id,
            roleName = user.role?.name
        )
    }

    override fun login(req: ReqLoginDto): ResLoginDto {
        val userEntityOpt = masterUserRepository.findFirstByUsername(req.username)
        if (userEntityOpt.isEmpty) {
            throw CustomException(
                "Invalid username or password",
                400
            )
        }

        if (!bcrypt.verify(req.password, userEntityOpt.get().password)) {
            throw CustomException(
                "Invalid username or password",
                400
            )
        }

        val token = jwtUtil.generateToken(userEntityOpt.get().id, userEntityOpt.get().role?.name ?: "user")
        return ResLoginDto(
            token = token
        )

    }
}