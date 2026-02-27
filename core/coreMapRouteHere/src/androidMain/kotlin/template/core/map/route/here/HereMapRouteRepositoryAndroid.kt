package template.core.map.route.here

import android.content.Context
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.core.engine.AuthenticationMode
import com.here.sdk.core.engine.SDKNativeEngine
import com.here.sdk.core.engine.SDKOptions
import com.here.sdk.core.errors.InstantiationErrorException
import com.here.sdk.routing.CarOptions
import com.here.sdk.routing.RoutingEngine
import com.here.sdk.routing.Waypoint
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.core.annotation.Factory
import template.core.heresdk.BuildConfig
import template.core.map.route.api.Location
import template.core.map.route.api.MapRouteRepository
import template.core.map.route.api.Route
import kotlin.coroutines.resume

@Factory
internal class HereMapRouteRepositoryAndroid(
    private val context: Context,
) : MapRouteRepository {
    override suspend fun getCarRoute(
        from: Location,
        to: Location,
    ): Route =
        suspendCancellableCoroutine { cont ->
            if (SDKNativeEngine.getSharedInstance() == null) {
                SDKNativeEngine.makeSharedInstance(
                    context,
                    SDKOptions(
                        AuthenticationMode.withKeySecret(
                            BuildConfig.HERE_ACCESS_KEY_ID,
                            BuildConfig.HERE_ACCESS_KEY_SECRET,
                        ),
                    ),
                )
            }
            val routingEngine =
                try {
                    RoutingEngine()
                } catch (e: InstantiationErrorException) {
                    cont.resume(Route(emptyList(), 0L, 0L))
                    return@suspendCancellableCoroutine
                }
            routingEngine.calculateRoute(
                listOf(
                    Waypoint(GeoCoordinates(from.lat, from.lon)),
                    Waypoint(GeoCoordinates(to.lat, to.lon)),
                ),
                CarOptions(),
            ) { routingError, routes ->
                if (routingError != null || routes.isNullOrEmpty()) {
                    cont.resume(Route(emptyList(), 0L, 0L))
                    return@calculateRoute
                }
                val hereRoute = routes.first()
                cont.resume(
                    Route(
                        polyline = hereRoute.geometry.vertices.map { Location(it.latitude, it.longitude) },
                        durationSeconds = hereRoute.duration.seconds,
                        distanceMeters = hereRoute.lengthInMeters.toLong(),
                    ),
                )
            }
        }
}
