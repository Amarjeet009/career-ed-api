package com.career.ed.user.dto

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("auth_identities")
data class AuthIdentity(
    @Id
    val id: Long? = null,
    @Column("user_id")
    val userId: UUID,
    val provider: String, // 'password' | 'google'
    @Column("provider_user_id")
    val providerUserId: String?,
    @Column("created_at")
    val createdAt: Instant? = null,
)