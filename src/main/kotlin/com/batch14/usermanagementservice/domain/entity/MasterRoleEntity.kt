package com.batch14.usermanagementservice.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.sql.Timestamp

@Entity
@Table(name = "mst_roles")
data class MasterRoleEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    var id: Int,

    @Column(name = "name")
    var name: String,

    @Column(name = "created_by")
    var createdBy: String? = null,

    @Column(name = "created_at", insertable = false, updatable = false)
    @CreationTimestamp
    var createdAt: Timestamp? = null,

    @Column(name = "updated_by")
    var updatedBy: String? = null,

    @Column(name = "updated_at", insertable = false, updatable = false)
    @UpdateTimestamp
    var updatedAt: Timestamp? = null,

    @Column(name = "deleted_at")
    var deletedAt: Timestamp? = null,

    @Column(name = "deleted_by")
    var deletedBy: String? = null,

    @Column(name = "is_active")
    var isActive: Boolean = true,

    @Column(name = "is_delete")
    var isDelete: Boolean = false,

    )