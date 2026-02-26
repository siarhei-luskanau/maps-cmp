package template.ui.map.route

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import template.core.location.api.LocationRepository
import template.core.map.route.api.Location
import template.core.map.route.api.MapRouteRepository
import template.ui.maps.view.api.MapsConfig

@Factory
class MapRouteViewModel(
    @InjectedParam private val navigationCallback: MapRouteNavigationCallback,
    @InjectedParam private val departureLat: Double?,
    @InjectedParam private val departureLon: Double?,
    @InjectedParam private val destinationLat: Double,
    @InjectedParam private val destinationLon: Double,
    private val mapRouteRepository: MapRouteRepository,
    private val locationRepository: LocationRepository,
) : ViewModel() {
    val viewState: StateFlow<MapRouteViewState>
        field = MutableStateFlow<MapRouteViewState>(MapRouteViewState.Loading)

    init {
        loadRoute()
    }

    fun onEvent(event: MapRouteViewEvent) {
        when (event) {
            MapRouteViewEvent.RetryRoute -> loadRoute()
            MapRouteViewEvent.NavigateBack -> navigationCallback.goBack()
        }
    }

    private fun loadRoute() {
        viewModelScope.launch {
            viewState.value = MapRouteViewState.Loading
            runCatching {
                val destination = Location(lat = destinationLat, lon = destinationLon)
                val from =
                    if (departureLat != null && departureLon != null) {
                        Location(lat = departureLat, lon = departureLon)
                    } else {
                        locationRepository.locationFlow().first()
                    }
                val config =
                    if (from != null) {
                        val route = mapRouteRepository.getCarRoute(from = from, to = destination)
                        MapsConfig(center = destination, currentLocation = from, route = route)
                    } else {
                        MapsConfig(center = destination, currentLocation = null, route = null)
                    }
                config
            }.onSuccess { config ->
                viewState.value = MapRouteViewState.Ready(config = config)
            }.onFailure { error ->
                viewState.value = MapRouteViewState.Error(message = error.message ?: "Unknown error")
            }
        }
    }
}
