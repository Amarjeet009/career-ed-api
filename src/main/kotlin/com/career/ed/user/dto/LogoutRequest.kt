package com.career.ed.user.dto

data class LogoutRequest(
    val userId: java.util.UUID,
    val refreshToken: String
)