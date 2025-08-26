package com.career.ed.user.repository

import com.career.ed.user.dto.User
import com.career.ed.user.dto.UserRole
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.UUID


@Repository
interface UserRepository : ReactiveCrudRepository<User, UUID> {
    fun findByEmail(email: String): Mono<User>
    fun findByPhone(phone: String): Mono<User>

}