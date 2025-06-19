package com.batch14.usermanagementservice.service.impl

import com.batch14.usermanagementservice.domain.Constant
import com.batch14.usermanagementservice.domain.dto.request.ReqLoginDto
import com.batch14.usermanagementservice.domain.dto.request.ReqRegisterDto
import com.batch14.usermanagementservice.domain.dto.request.ReqUpdateUserDto
import com.batch14.usermanagementservice.domain.dto.request.ReqVerifyOtpDto
import com.batch14.usermanagementservice.domain.dto.response.ResGetUserDto
import com.batch14.usermanagementservice.domain.dto.response.ResLoginDto
import com.batch14.usermanagementservice.domain.dto.response.ResRegisterDto
import com.batch14.usermanagementservice.domain.entity.MasterUserEntity
import com.batch14.usermanagementservice.exception.CustomException
import com.batch14.usermanagementservice.repository.MasterRoleRepository
import com.batch14.usermanagementservice.repository.MasterUserRepository
import com.batch14.usermanagementservice.service.MasterRoleService
import com.batch14.usermanagementservice.service.MasterUserService
import com.batch14.usermanagementservice.utils.BCryptUtil
import com.batch14.usermanagementservice.utils.JwtUtil
import com.batch14.usermanagementservice.utils.OtpUtil
import jakarta.servlet.http.HttpServletRequest
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Duration
import java.util.Optional

@Service
class MasterUserServiceImpl(
    private val masterUserRepository: MasterUserRepository,
    private val masterRoleService: MasterRoleService,
    private val stringRedisTemplate: StringRedisTemplate,
    private val masterRoleRepository: MasterRoleRepository,
    private val bcrypt: BCryptUtil,
    private val jwtUtil: JwtUtil,
    private val otpUtil: OtpUtil
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

    //    kalo belum ada data di redis bakal disimpan
    //    kalo sudah ada akan update
    @Cacheable(
        "getUserById",
        key = "{#id}"
    )
    override fun findUserById(id: Int): ResGetUserDto? {
        val rawData = masterUserRepository.findByIdAndNotDeleted(id)
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


    override fun findUserByIds(ids: List<Int>): List<ResGetUserDto> {
        val rawData = masterUserRepository.findAllByIds(ids)
        val result = mutableListOf<ResGetUserDto>()
        rawData.forEach { u ->
            result.add(
                ResGetUserDto(
                    username = u.username,
                    id = u.id,
                    email = u.email,
                    roleId = u.role?.id,
                    roleName = u.role?.name,
                )
            )
        }
        return result
    }

    override fun register(req: ReqRegisterDto): ResRegisterDto {

        val role = masterRoleRepository.findById(1)


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

        val hashPw = bcrypt.hash(req.password)

        val savedUser = masterUserRepository.save(
            MasterUserEntity(
                email = req.email,
                password = hashPw,
                username = req.username,
                role = role.orElse(null),
                isActive = false,
            )
        )

        //        generate otp
        val otp = otpUtil.generateOtp()
        val operationString = stringRedisTemplate.opsForValue()
        operationString.set("user:otp:${req.email}", otp, Duration.ofMinutes(5))

        return ResRegisterDto(
            otp = otp,
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

        if (!userEntityOpt.get().isActive) {
            throw CustomException(
                "User is not active yet",
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


    @CacheEvict(
        value = ["getUserById"],
        key = "{#userId}"
    )
    override fun updateUser(
        req: ReqUpdateUserDto,
        userId: Int
    ): ResGetUserDto {
//        val userId = httpServletRequest.getHeader(Constant.HEADER_USER_ID)
        val user = masterUserRepository.findByIdAndNotDeleted(userId).orElseThrow {
            throw CustomException(
                "User not found",
                HttpStatus.BAD_REQUEST.value()
            )
        }

        val existingUser = masterUserRepository.findFirstByUsername(req.username)
        if (existingUser.isPresent) {
            if (existingUser.get().id != user.id) {
                throw CustomException(
                    "username telah terdaftar",
                    HttpStatus.BAD_REQUEST.value()
                )
            }
        }

        val existingUserEmail = masterUserRepository.findFirstByEmail(req.email)
        if (existingUserEmail != null && existingUserEmail.id != user.id) {
            throw CustomException(
                "username telah terdaftar",
                HttpStatus.BAD_REQUEST.value()
            )

        }

        user.email = req.email
        user.username = req.username
        user.updatedBy = userId.toString()

        val result = masterUserRepository.save(user)
        return ResGetUserDto(
            id = result.id,
            username = result.username,
            email = result.email,
            roleId = result.role?.id,
            roleName = result.role?.name
        )


    }

    override fun hardDeleteUser(userId: Int, performerId: Int) {
        val performer = masterUserRepository.findUserWithRolesById(performerId).orElseThrow {
            throw CustomException(
                "Performer not found",
                HttpStatus.NOT_FOUND.value()
            )
        }

        if (performer.role?.name != "admin") {
            throw CustomException(
                "Only administrators can perform hard delete",
                HttpStatus.FORBIDDEN.value()
            )
        }

        if (userId == performerId) {
            throw CustomException(
                "You cannot hard delete yourself",
                HttpStatus.FORBIDDEN.value()
            )
        }

        val user = masterUserRepository.findById(userId).orElseThrow {
            throw CustomException(
                "User not found",
                HttpStatus.BAD_REQUEST.value()
            )
        }

        masterUserRepository.delete(user)
    }

    override fun softDeleteUser(userId: Int, performerId: Int) {

        if (userId == performerId) {
            throw CustomException(
                "You cannot soft delete yourself",
                HttpStatus.FORBIDDEN.value()
            )
        }

        val user = masterUserRepository.findByIdAndNotDeleted(userId).orElseThrow {
            throw CustomException(
                "User not found",
                HttpStatus.BAD_REQUEST.value()
            )
        }

        user.isDelete = true
        user.deletedAt = Timestamp(System.currentTimeMillis())
        user.deletedBy = performerId.toString()

        masterUserRepository.save(user)

    }


    override fun verifyOtp(req: ReqVerifyOtpDto) {
        val cachedOtp = stringRedisTemplate.opsForValue().get("user:otp:${req.email}")
            ?: throw CustomException("OTP expired or not found", 400)

        if (cachedOtp != req.otp) {
            throw CustomException("Invalid OTP", 400)
        }

        val user = masterUserRepository.findFirstByEmail(req.email)
            ?: throw CustomException("User not found", 404)

        user.isActive = true
        masterUserRepository.save(user)

        stringRedisTemplate.delete("user:otp:${req.email}")
    }
}