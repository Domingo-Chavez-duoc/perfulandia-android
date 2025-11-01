package com.domichav.perfulandia.ui.navigation


sealed class NavigationEvent {
    //Evento para destino especifico
    data class NavigateTo(
        val route: Screen,
        val popUpToRoute: Screen? = null,
        val inclusive: Boolean = false,
        val singleTop: Boolean = false
    ) : NavigationEvent()

    //Evento para volver a la pantalla anterior
    object PopBackStack : NavigationEvent()

    //Evento para navegar "hacia arriba" en la jerarquia de la app
    object NavigateUp : NavigationEvent()
}