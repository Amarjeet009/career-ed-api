package com.career.ed.auth.util

data class ApiResponse<T>(
    val status: Int,
    val message: String,
    val data: T
)