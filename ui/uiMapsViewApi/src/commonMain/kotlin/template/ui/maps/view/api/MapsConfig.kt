package template.ui.maps.view.api

import template.core.map.route.api.Location
import template.core.map.route.api.Route

data class MapsConfig(
    val center: Location,
    val zoom: Float = 14f,
    val currentLocation: Location?,
    val route: Route?,
)
