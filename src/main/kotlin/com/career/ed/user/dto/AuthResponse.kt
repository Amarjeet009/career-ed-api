package com.career.ed.user.dto

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val userId: java.util.UUID
)