package com.batch14.usermanagementservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class UserManagementServiceApplication

fun main(args: Array<String>) {
	runApplication<UserManagementServiceApplication>(*args)
}
