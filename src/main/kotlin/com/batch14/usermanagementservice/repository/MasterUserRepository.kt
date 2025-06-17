package com.batch14.usermanagementservice.repository

import com.batch14.usermanagementservice.domain.entity.MasterUserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface MasterUserRepository : JpaRepository<MasterUserEntity, Int> {
    @Query(
        """
            SELECT U FROM MasterUserEntity U
            WHERE U.isActive = true 
            AND U.isDelete = false
        """, nativeQuery = false
    )
    fun findAllActiveRoles(): List<MasterUserEntity>

    @Query(
        """
            SELECT DISTINCT u FROM MasterUserEntity u 
            LEFT JOIN FETCH u.role r 
            WHERE u.id = :id 
            AND u.isDelete = false
        """, nativeQuery = false
    )
    fun findByIdAndNotDeleted(id: Int): Optional<MasterUserEntity>

    @Query(
        """
            
        """
    )
    fun findUserWithRolesById(id: Int): Optional<MasterUserEntity>
    fun findFirstByEmail(email: String): MasterUserEntity?
    fun findFirstByUsername(username: String): Optional<MasterUserEntity?>

    @Query(
        """
            SELECT U FROM MasterUserEntity U
            WHERE U.id IN (:ids)
        """, nativeQuery = false
    )
    fun findAllByIds(ids: List<Int>): List<MasterUserEntity>
}