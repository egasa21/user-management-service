package com.batch14.usermanagementservice.repository

import com.batch14.usermanagementservice.domain.entity.MasterRoleEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface MasterRoleRepository : JpaRepository<MasterRoleEntity, Int> {

}