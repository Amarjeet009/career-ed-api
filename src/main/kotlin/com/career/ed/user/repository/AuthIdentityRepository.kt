package com.career.ed.user.repository

import com.career.ed.user.dto.AuthIdentity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthIdentityRepository: ReactiveCrudRepository<AuthIdentity, Long> {
}