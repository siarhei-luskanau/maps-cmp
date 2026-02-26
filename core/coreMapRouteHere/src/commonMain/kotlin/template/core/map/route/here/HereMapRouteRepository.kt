package template.core.map.route.here

import org.koin.core.annotation.Factory
import template.core.map.route.api.Location
import template.core.map.route.api.MapRouteRepository
import template.core.map.route.api.Route

@Factory
class HereMapRouteRepository : MapRouteRepository {
    override suspend fun getCarRoute(
        from: Location,
        to: Location,
    ): Route = Route(polyline = emptyList(), durationSeconds = 0L, distanceMeters = 0L)
}
