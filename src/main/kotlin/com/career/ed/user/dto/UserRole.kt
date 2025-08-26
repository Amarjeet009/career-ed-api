package com.career.ed.user.dto

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("user_roles")
data class UserRole(
    @Column("user_id")
    val userId: UUID,
    val role: String, // 'student' | 'instructor' | 'admin'
)