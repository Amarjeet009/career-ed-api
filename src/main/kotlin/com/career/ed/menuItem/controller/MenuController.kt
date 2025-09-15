package com.career.ed.menuItem.controller

import com.career.ed.auth.util.ApiResponse
import com.career.ed.menuItem.dto.MenuItemDto
import com.career.ed.menuItem.services.MenuService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1")
class MenuController(private val service: MenuService) {

    @GetMapping("/menu")
    fun getMenu(@RequestParam role: String): Mono<ApiResponse<List<MenuItemDto>>> {
        return service.getMenuForRole(role)
    }

}
