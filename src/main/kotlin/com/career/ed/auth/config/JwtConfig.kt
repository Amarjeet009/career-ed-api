package com.career.ed.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@EnableConfigurationProperties(JwtProps::class)
class JwtConfig


@ConfigurationProperties(prefix = "app.jwt")
data class JwtProps(
    var issuer: String = "",
    var accessTtlMinutes: Long = 15,
    var refreshTtlDays: Long = 30,
    var hs256Secret: String = ""
)