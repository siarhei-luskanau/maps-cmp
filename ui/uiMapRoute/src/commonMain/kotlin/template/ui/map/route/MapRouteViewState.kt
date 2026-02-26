package template.ui.map.route

import template.ui.maps.view.api.MapsConfig

sealed interface MapRouteViewState {
    data object Loading : MapRouteViewState

    data class Ready(
        val config: MapsConfig,
    ) : MapRouteViewState

    data class Error(
        val message: String,
    ) : MapRouteViewState
}
