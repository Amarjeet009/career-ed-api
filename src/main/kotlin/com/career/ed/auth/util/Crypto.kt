package com.career.ed.auth.util

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64


object Crypto {
private val rng = SecureRandom()
fun randomToken(bytes: Int = 32): String { // 256-bit
val b = ByteArray(bytes)
rng.nextBytes(b)
return Base64.getUrlEncoder().withoutPadding().encodeToString(b)
}
fun sha256(base64UrlToken: String): String {
val md = MessageDigest.getInstance("SHA-256")
val dig = md.digest(base64UrlToken.toByteArray())
return Base64.getEncoder().encodeToString(dig)
}
}