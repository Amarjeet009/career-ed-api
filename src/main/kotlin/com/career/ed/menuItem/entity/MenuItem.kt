package com.career.ed.menuItem.entity


import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("menu_items")
data class MenuItem(
    @Id val id: Long? = null,
    val title: String,
    val path: String?,
    val newTab: Boolean = false,
    val parentId: Long? = null,
    val roles: List<String> = listOf("guest")
)