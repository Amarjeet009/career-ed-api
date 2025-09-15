package com.career.ed.menuItem.dto

data class MenuItemDto(
    val id: Long,
    val title: String,
    val path: String?,
    val newTab: Boolean,
    val submenu: List<MenuItemDto> = emptyList()
)