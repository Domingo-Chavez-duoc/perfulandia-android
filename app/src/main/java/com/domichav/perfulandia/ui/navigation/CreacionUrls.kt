package com.domichav.perfulandia.ui.navigation

sealed class Screen(val route: String) {
    //Rutas simples (sin argumentos)
    data object Home : Screen("home_page")
    data object Profile : Screen("profile_page")
    //data object Settings : Screen("settings_page")

    //Ruta con argumentos (itemId) encapsulada en un data class (escalable a mas parametros como userName, etc)
    data class Detail(val itemId: String) : Screen("detail_page/{itemId}") {
        fun  buildRoute(): String {
            return route.replace("{itemId}", itemId)
        }
    }
}