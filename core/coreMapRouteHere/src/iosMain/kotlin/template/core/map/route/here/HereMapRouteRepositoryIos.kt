package template.core.map.route.here

import org.koin.core.annotation.Factory
import template.core.common.CoreResult
import template.core.common.Location
import template.core.map.route.api.MapRouteRepository
import template.core.map.route.api.Route

@Factory
internal class HereMapRouteRepositoryIos : MapRouteRepository {
    override suspend fun getCarRoute(
        from: Location,
        to: Location,
    ): CoreResult<Route> = CoreResult.Success(Route(polyline = emptyList(), durationSeconds = 0L, distanceMeters = 0L))
}
