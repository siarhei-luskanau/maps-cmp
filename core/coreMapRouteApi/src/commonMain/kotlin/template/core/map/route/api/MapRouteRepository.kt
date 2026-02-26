package template.core.map.route.api

interface MapRouteRepository {
    suspend fun getCarRoute(
        from: Location,
        to: Location,
    ): Route
}
