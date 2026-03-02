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
import template.core.common.CoreResult
import template.core.common.Location
import template.core.heresdk.BuildConfig
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
    ): CoreResult<Route> =
        suspendCancellableCoroutine { cont ->
            try {
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
                val routingEngine = RoutingEngine()
                routingEngine.calculateRoute(
                    listOf(
                        Waypoint(GeoCoordinates(from.lat, from.lon)),
                        Waypoint(GeoCoordinates(to.lat, to.lon)),
                    ),
                    CarOptions(),
                ) { routingError, routes ->
                    when {
                        routingError != null -> {
                            cont.resume(CoreResult.Failure(Error(routingError.name)))
                        }

                        else -> {
                            val hereRoute = routes.orEmpty().first()
                            cont.resume(
                                CoreResult.Success(
                                    Route(
                                        polyline = hereRoute.geometry.vertices.map { Location(it.latitude, it.longitude) },
                                        durationSeconds = hereRoute.duration.seconds,
                                        distanceMeters = hereRoute.lengthInMeters.toLong(),
                                    ),
                                ),
                            )
                        }
                    }
                }
            } catch (e: InstantiationErrorException) {
                cont.resume(CoreResult.Failure(e))
            }
        }
}
