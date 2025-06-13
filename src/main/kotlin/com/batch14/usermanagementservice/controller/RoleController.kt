package com.batch14.usermanagementservice.controller

import com.batch14.usermanagementservice.domain.dto.response.ResGetAllRoleDto
import com.batch14.usermanagementservice.service.MasterRoleService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/role")
class RoleController(
    private val masterRoleService: MasterRoleService
) {

    @GetMapping("/all")
    fun getAllRoles(): ResponseEntity<List<ResGetAllRoleDto>> {
        return ResponseEntity.ok(
            masterRoleService.getAllRoles()
        )
    }
}