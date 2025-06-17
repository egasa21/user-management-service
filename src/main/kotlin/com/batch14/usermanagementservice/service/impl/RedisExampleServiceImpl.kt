package com.batch14.usermanagementservice.service.impl

import com.batch14.usermanagementservice.exception.CustomException
import com.batch14.usermanagementservice.repository.MasterUserRepository
import com.batch14.usermanagementservice.service.RedisExampleService
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class RedisExampleServiceImpl(
    private val stringRedisTemplate: StringRedisTemplate,
    private val masterUserRepository: MasterUserRepository
) : RedisExampleService {
    override fun set(userId: Int): String {
        val user = masterUserRepository.findById(userId).orElseThrow {
            throw CustomException("User not found", 404)
        }

        val operationString = stringRedisTemplate.opsForValue()

        operationString.set("user:username:${user.id}", user.username, Duration.ofSeconds(60))

        return "User with ID ${user.id} has been set in redis"
    }

    override fun get(userId: Int): String? {
        val user = masterUserRepository.findById(userId).orElseThrow {
            throw CustomException("User not found", 404)
        }

        val operationString = stringRedisTemplate.opsForValue()

        val name = operationString.get("user:username:${user.id}")
            ?: throw CustomException("User not found in redis", 404)

        return "User ID: ${user.id}, Username: $name"
    }
}