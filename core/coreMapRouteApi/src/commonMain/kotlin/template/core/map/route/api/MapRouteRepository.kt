package template.core.map.route.api

import template.core.common.CoreResult
import template.core.common.Location

interface MapRouteRepository {
    suspend fun getCarRoute(
        from: Location,
        to: Location,
    ): CoreResult<Route>
}
