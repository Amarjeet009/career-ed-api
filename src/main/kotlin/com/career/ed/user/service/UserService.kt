package com.career.ed.user.service

import com.career.ed.auth.entity.RefreshToken
import com.career.ed.auth.repository.RefreshTokenRepository
import com.career.ed.auth.service.JwtService
import com.career.ed.common.service.RedisService
import com.career.ed.user.dto.AuthIdentity
import com.career.ed.user.dto.AuthResponse
import com.career.ed.user.dto.LoginRequest
import com.career.ed.user.dto.SignupRequest
import com.career.ed.user.dto.User
import com.career.ed.user.dto.UserRole
import com.career.ed.user.repository.AuthIdentityRepository
import com.career.ed.user.repository.UserRepository
import com.career.ed.user.repository.UserRoleRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val authIdentityRepository: AuthIdentityRepository,
    private val userRoleRepository: UserRoleRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val redisService: RedisService
) {

    fun signup(req: SignupRequest): Mono<AuthResponse> {
        return userRepository.findByEmail(req.email)
            .flatMap<User> { Mono.error(IllegalStateException("Email already registered")) }
            .switchIfEmpty(
                Mono.defer {
                    val user = User(
                        email = req.email,
                        passwordHash = passwordEncoder.encode(req.password),
                        displayName = req.displayName,
                        status = "active"
                    )
                    userRepository.save(user)
                }
            )
            .flatMap { user ->
                authIdentityRepository.save(
                    AuthIdentity(
                        userId = user.id!!,
                        provider = "password",
                        providerUserId = user.email
                    )
                ).then(userRoleRepository.save(UserRole(userId = user.id!!, role = "student")))
                 .thenReturn(user)
            }
            .flatMap { generateTokens(it) }
    }

    fun login(req: LoginRequest): Mono<AuthResponse> {
        return userRepository.findByEmail(req.email)
            .switchIfEmpty(Mono.error(IllegalArgumentException("Invalid email/password")))
            .flatMap { user ->
                if (!passwordEncoder.matches(req.password, user.passwordHash)) {
                    Mono.error(IllegalArgumentException("Invalid email/password"))
                } else {
                    generateTokens(user)
                }
            }
    }

    private fun generateTokens(user: User): Mono<AuthResponse> {
        // FIX: renamed generateAccessToken -> issueAccessToken
        val accessToken = jwtService.issueAccessToken(user.id!!, listOf("student"))

        val refreshTokenRaw = UUID.randomUUID().toString()
        val refreshTokenHash = passwordEncoder.encode(refreshTokenRaw)

        val refreshToken = RefreshToken(
            userId = user.id!!,
            tokenHash = refreshTokenHash,
            expiresAt = Instant.now().plus(30, ChronoUnit.DAYS)
        )

        return refreshTokenRepository.save(refreshToken)
            .doOnSuccess {
                // store in redis for faster validation
                redisService.set(
                    "refresh:${user.id}:${refreshToken.id}",
                    refreshTokenRaw,
                    Duration.ofDays(30) // ✅ use proper Duration instead of raw seconds
                ).subscribe()
            }
            .map {
                AuthResponse(
                    accessToken = accessToken,
                    refreshToken = refreshTokenRaw,
                    userId = user.id!!
                )
            }
    }


    fun refreshToken(refreshTokenRaw: String): Mono<AuthResponse> {
        // Step 1: Check in DB first (because Redis key pattern is not efficient)
        return refreshTokenRepository.findAll() // you can add a custom repo method findByTokenHash
            .filter { passwordEncoder.matches(refreshTokenRaw, it.tokenHash) }
            .next() // now this works, because findAll() returns a Flux
            .switchIfEmpty(Mono.error(IllegalArgumentException("Invalid or expired refresh token")))
            .flatMap { storedToken ->
                if (storedToken.expiresAt.isBefore(Instant.now())) {
                    return@flatMap Mono.error<AuthResponse>(IllegalArgumentException("Token expired"))
                }

                userRepository.findById(storedToken.userId!!)
                    .switchIfEmpty(Mono.error(IllegalArgumentException("User not found")))
                    .flatMap { generateTokens(it) }
            }
    }


    fun logout(userId: UUID, refreshTokenRaw: String): Mono<Void> {
        return refreshTokenRepository.findAllByUserId(userId)
            .filterWhen { rt -> redisService.get("refresh:${userId}:${rt.id}").map { it == refreshTokenRaw } }
            .next()
            .flatMap { rt ->
                rt.revokedAt = Instant.now()
                refreshTokenRepository.save(rt)
            }
            .flatMap { rt ->
                redisService.delete("refresh:${userId}:${rt.id}").then()  // ✅ return Mono<Void>
            }
    }



}