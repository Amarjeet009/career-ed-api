package com.career.ed.menuItem.repository

import com.career.ed.menuItem.entity.MenuItem
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface MenuItemRepository : ReactiveCrudRepository<MenuItem, Long> {
    fun findByParentId(parentId: Long?): Flux<MenuItem>
    fun findByRolesContaining(role: String): Flux<MenuItem>
    fun findByParentIdAndRolesContaining(parentId: Long?, role: String): Flux<MenuItem>
    @Query("""
        SELECT * FROM menu_items
        WHERE parent_id IS NULL AND :role = ANY(roles)
    """)
    fun findTopLevelByRole(role: String): Flux<MenuItem>
    @Query("""
        SELECT * FROM menu_items
        WHERE parent_id = :parentId AND :role = ANY(roles)
    """)
    fun findSubmenusByParentAndRole(parentId: Long, role: String): Flux<MenuItem>



}
