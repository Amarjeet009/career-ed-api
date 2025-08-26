package com.career.ed.user.dto

data class SignupRequest(
    val email: String,
    val password: String,
    val displayName: String
)