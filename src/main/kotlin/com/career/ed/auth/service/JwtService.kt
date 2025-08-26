package com.career.ed.auth.service

import com.career.ed.auth.config.JwtProps
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.UUID
import org.springframework.stereotype.Service

/**
 * JWT utility for issuing and validating short-lived access tokens.
 * - HS256 (symmetric) using secret from application config
 * - 'sub' = userId (UUID)
 * - 'roles' = list of role strings ["student","instructor","admin"]
 * - 'iss' = app.jwt.issuer
 * - expiry = now + app.jwt.access-ttl-minutes
 */
@Service
class JwtService(
    private val props: JwtProps
) {

    // Nimbus requires byte[] secrets; for HS256 >= 256 bits (32 bytes) is recommended
    private val secretBytes: ByteArray by lazy { props.hs256Secret.toByteArray(Charsets.UTF_8) }
    private val signer by lazy { MACSigner(secretBytes) }
    private val verifier by lazy { MACVerifier(secretBytes) }

    data class ParsedToken(
        val subject: UUID,
        val roles: List<String>,
        val issuedAt: Instant,
        val expiresAt: Instant,
        val jwtId: String?,
        val rawClaims: JWTClaimsSet
    )

    /**
     * Issue a signed JWT access token.
     *
     * @param userId Subject (UUID)
     * @param roles  e.g. listOf("student")
     * @param extraClaims optional extra public claims (must be JSON-serializable)
     */
    fun issueAccessToken(
        userId: UUID, roles: List<String>, extraClaims: Map<String, Any?> = emptyMap()
    ): String {
        val now = Instant.now()
        val exp = now.plus(props.accessTtlMinutes, ChronoUnit.MINUTES)

        val builder = JWTClaimsSet.Builder().subject(userId.toString()).issuer(props.issuer).issueTime(Date.from(now))
            .expirationTime(Date.from(exp)).claim("roles", roles)

        // attach extra claims (ignore nulls)
        for ((k, v) in extraClaims) if (v != null) builder.claim(k, v)

        val claims = builder.build()
        val jwt = SignedJWT(JWSHeader.Builder(JWSAlgorithm.HS256).build(), claims)
        jwt.sign(signer)
        return jwt.serialize()
    }

    /**
     * Parse and validate an access token.
     *
     * @param token JWT string (without "Bearer ")
     * @param leewaySeconds clock skew leeway for 'exp' and 'iat'
     * @return ParsedToken or null if invalid
     */
    fun parseAccessToken(token: String, leewaySeconds: Long = 60): ParsedToken? {
        return try {
            val signed = SignedJWT.parse(token)

            // 1) Signature
            if (!signed.verify(verifier)) return null

            // 2) Claims basic validation
            val claims = signed.jwtClaimsSet
            val now = Instant.now()
            val exp = claims.expirationTime?.toInstant() ?: return null
            val iat = claims.issueTime?.toInstant() ?: return null

            // leeway handling
            if (now.isAfter(exp.plusSeconds(leewaySeconds))) return null
            if (iat.isAfter(now.plusSeconds(leewaySeconds))) return null

            // Issuer check (optional; enforce if configured)
            if (props.issuer.isNotBlank() && claims.issuer != props.issuer) return null

            // Subject UUID
            val subject = try {
                UUID.fromString(claims.subject)
            } catch (_: Exception) {
                return null
            }

            // Roles as list of strings
            val rolesClaim = claims.getClaim("roles")
            val roles: List<String> = when (rolesClaim) {
                is List<*> -> rolesClaim.filterIsInstance<String>()
                is Array<*> -> rolesClaim.filterIsInstance<String>()
                is String -> listOf(rolesClaim)
                null -> emptyList()
                else -> emptyList()
            }

            ParsedToken(
                subject = subject,
                roles = roles,
                issuedAt = iat,
                expiresAt = exp,
                jwtId = claims.jwtid,
                rawClaims = claims
            )
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Convenience to pull a Bearer token from an Authorization header.
     * Returns null if header missing or malformed.
     */
    fun fromAuthHeader(authHeader: String?): String? {
        if (authHeader.isNullOrBlank()) return null
        val parts = authHeader.trim().split(' ', limit = 2)
        if (parts.size != 2) return null
        if (!parts[0].equals("Bearer", ignoreCase = true)) return null
        return parts[1].takeIf { it.isNotBlank() }
    }



}