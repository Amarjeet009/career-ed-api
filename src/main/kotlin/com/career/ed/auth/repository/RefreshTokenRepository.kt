package com.career.ed.auth.repository

import com.career.ed.auth.entity.RefreshToken
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID


@Repository
interface RefreshTokenRepository : ReactiveCrudRepository<RefreshToken, Long> {
    fun findByTokenHash(tokenHash: String): Mono<RefreshToken>
    fun findAllByUserId(userId: UUID): Flux<RefreshToken>
}