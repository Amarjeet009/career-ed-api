package com.career.ed.auth.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID



@Table("refresh_tokens")
data class RefreshToken(
    @Id val id: Long? = null,
    @Column("user_id")
    val userId: UUID,
    @Column("token_hash")
    val tokenHash: String,
    @Column("device_id") val
    deviceId: String? = null,
    @Column("user_agent")
    val userAgent: String? = null,
    @Column("ip_address")
    val ipAddress: String? = null,
    @Column("created_at")
    val createdAt: Instant? = null,
    @Column("expires_at")
    val expiresAt: Instant,
    @Column("revoked_at")
    var revokedAt: Instant? = null,
)