package com.career.ed.auth.service

import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Component


@Component
class PasswordHasher {
    fun hash(raw: String): String = BCrypt.hashpw(raw, BCrypt.gensalt())
    fun matches(raw: String, hashed: String): Boolean = BCrypt.checkpw(raw, hashed)
}