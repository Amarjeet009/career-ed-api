package com.career.ed.auth.config

import com.career.ed.auth.service.JwtService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import reactor.core.publisher.Mono

@Configuration
@EnableReactiveMethodSecurity
class SecurityConfig(private val jwtService: JwtService) {


    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()


    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .authorizeExchange { ex ->
                ex.pathMatchers("/auth/**").permitAll()
                ex.anyExchange().authenticated()
            }
            .authenticationManager(jwtAuthManager())
            .build()
    }



    @Bean
    fun jwtAuthManager(): ReactiveAuthenticationManager = ReactiveAuthenticationManager { auth ->
        val token = auth.credentials as? String ?: return@ReactiveAuthenticationManager Mono.empty()
        val parsed = jwtService.parseAccessToken(token) ?: return@ReactiveAuthenticationManager Mono.empty()
        val authorities = parsed.roles.map { SimpleGrantedAuthority("ROLE_$it") }
        val authentication: Authentication = UsernamePasswordAuthenticationToken(parsed.subject, token, authorities)
        Mono.just(authentication)
    }


    @Bean
    fun bearerConverter(): ServerAuthenticationConverter = ServerAuthenticationConverter { exchange ->
        val header = exchange.request.headers.getFirst("Authorization") ?: return@ServerAuthenticationConverter Mono.empty()
        val token = if (header.startsWith("Bearer ", true)) header.substring(7) else null
        token?.let { Mono.just(UsernamePasswordAuthenticationToken(null, it)) } ?: Mono.empty()
    }
}
