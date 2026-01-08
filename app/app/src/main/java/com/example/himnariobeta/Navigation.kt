package com.example.himnariobeta

/**
 * Sistema de navegación type-safe para la aplicación
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Lists : Screen("lists")
    object Filters : Screen("filters")
    object ListDetail : Screen("list_detail/{listId}") {
        fun createRoute(listId: Int) = "list_detail/$listId"
        const val ARG_LIST_ID = "listId"
    }
    object FolderDetail : Screen("folder_detail/{folderId}") {
        fun createRoute(folderId: Int) = "folder_detail/$folderId"
        const val ARG_FOLDER_ID = "folderId"
    }
}

/**
 * Rutas de navegación principal
 */
object MainDestinations {
    const val HOME_ROUTE = "home"
    const val LISTS_ROUTE = "lists"
    const val FILTERS_ROUTE = "filters"
}
