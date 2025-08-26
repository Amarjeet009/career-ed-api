package com.career.ed.user.controller


import com.career.ed.user.dto.AuthResponse
import com.career.ed.user.dto.LoginRequest
import com.career.ed.user.dto.LogoutRequest
import com.career.ed.user.dto.RefreshRequest
import com.career.ed.user.dto.SignupRequest
import com.career.ed.user.service.UserService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/auth")
class AuthController(
    private val userService: UserService
) {

    @PostMapping("/signup")
    fun signup(@RequestBody req: SignupRequest): Mono<AuthResponse> {
        return userService.signup(req)
    }

    @PostMapping("/login")
    fun login(@RequestBody req: LoginRequest): Mono<AuthResponse> {
        return userService.login(req)
    }

    @PostMapping("/refresh")
    fun refresh(@RequestBody req: RefreshRequest): Mono<AuthResponse> {
        return userService.refreshToken(req.refreshToken)
    }

    @PostMapping("/logout")
    fun logout(@RequestBody req: LogoutRequest): Mono<Void> {
        return userService.logout(req.userId, req.refreshToken)
    }

}