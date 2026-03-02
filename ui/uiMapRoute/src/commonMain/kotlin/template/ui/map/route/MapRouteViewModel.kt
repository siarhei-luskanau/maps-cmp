package template.ui.map.route

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import template.core.common.CoreResult
import template.core.common.Location
import template.core.map.route.api.MapRouteRepository
import template.ui.maps.view.api.MapsConfig

@Factory
class MapRouteViewModel(
    @InjectedParam private val navigationCallback: MapRouteNavigationCallback,
    @InjectedParam private val departureLat: Double,
    @InjectedParam private val departureLon: Double,
    @InjectedParam private val destinationLat: Double,
    @InjectedParam private val destinationLon: Double,
    private val mapRouteRepository: MapRouteRepository,
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
            val from = Location(lat = departureLat, lon = departureLon)
            val destination = Location(lat = destinationLat, lon = destinationLon)
            when (val result = mapRouteRepository.getCarRoute(from = from, to = destination)) {
                is CoreResult.Success -> {
                    val config = MapsConfig(center = destination, currentLocation = from, route = result.result)
                    viewState.value = MapRouteViewState.Ready(config = config)
                }

                is CoreResult.Failure -> {
                    viewState.value = MapRouteViewState.Error(message = result.error.message ?: "Unknown error")
                }
            }
        }
    }
}
