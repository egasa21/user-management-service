package com.batch14.usermanagementservice.service

interface RedisExampleService {
    fun set(userId: Int) : String
    fun get(userId: Int) : String?
}