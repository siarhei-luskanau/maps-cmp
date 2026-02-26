package template.ui.map.route

sealed interface MapRouteViewEvent {
    data object RetryRoute : MapRouteViewEvent

    data object NavigateBack : MapRouteViewEvent
}
