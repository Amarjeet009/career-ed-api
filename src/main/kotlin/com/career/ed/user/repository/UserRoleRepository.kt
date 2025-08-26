package com.career.ed.user.repository

import com.career.ed.user.dto.UserRole
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.util.UUID


@Repository
interface UserRoleRepository : ReactiveCrudRepository<UserRole, Long> {
    fun findByUserId(userId: UUID): Flux<UserRole>
}