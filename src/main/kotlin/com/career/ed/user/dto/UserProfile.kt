package com.career.ed.user.dto

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("user_profiles")
data class UserProfile(
    @Id @Column("user_id")
    val userId: UUID,
    val bio: String? = null,
    val headline: String? = null,
    val interests: Array<String>? = null,
    @Column("social_links")
    val socialLinks: String? = null, // map as JSON string if needed
    val settings: String? = null,
    @Column("updated_at")
    val updatedAt: Instant? = null,
)