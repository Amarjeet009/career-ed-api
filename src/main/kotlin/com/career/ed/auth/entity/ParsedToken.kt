package com.career.ed.auth.entity

import java.util.UUID

data class ParsedToken(
    val subject: UUID,
    val roles: List<String>
)