package com.career.ed.menuItem.services

import com.career.ed.auth.util.ApiResponse
import com.career.ed.menuItem.dto.MenuItemDto
import com.career.ed.menuItem.repository.MenuItemRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class MenuService(private val repo: MenuItemRepository) {

    fun getMenuForRole(role: String): Mono<ApiResponse<List<MenuItemDto>>> {
        return repo.findTopLevelByRole(role)
            .flatMap { parent ->
                repo.findSubmenusByParentAndRole(parent.id!!, role)
                    .collectList()
                    .map { submenu ->
                        MenuItemDto(
                            id = parent.id!!,
                            title = parent.title,
                            path = parent.path,
                            newTab = parent.newTab,
                            submenu = submenu.map {
                                MenuItemDto(
                                    id = it.id!!,
                                    title = it.title,
                                    path = it.path,
                                    newTab = it.newTab
                                )
                            }
                        )
                    }
            }
            .collectList()
            .map { menu ->
                if (menu.isNotEmpty()) {
                    ApiResponse(200, "success", menu)
                } else {
                    ApiResponse(400, "records not found", emptyList())
                }
            }
    }



}
