package com.career.ed.user.dto

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.time.LocalDate
import java.util.UUID


@Table("users")
data class User(
    @Id
    var id: UUID? = null,
    @Column("email")
    var email: String? = null,
    @Column("email_verified_at")
    var emailVerifiedAt: Instant? = null,
    @Column("password_hash")
    var passwordHash: String? = null,
    @Column("phone")
    var phone: String? = null,
    @Column("phone_verified_at")
    var phoneVerifiedAt: Instant? = null,
    @Column("display_name")
    var displayName: String? = null,
    @Column("first_name")
    var firstName: String? = null,
    @Column("last_name")
    var lastName: String? = null,
    @Column("avatar_url")
    var avatarUrl: String? = null,
    @Column("dob")
    var dob: Instant? = null,
    @Column("gender")
    var gender: String? = null,
    @Column("country_code")
    var countryCode: String? = null,
    @Column("status")
    var status: String = "active",
    @Column("created_at")
    var createdAt: Instant = Instant.now(),
    @Column("updated_at")
    var updatedAt: Instant = Instant.now()
)