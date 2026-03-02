package template.core.map.route.here

import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.core.annotation.Factory
import template.core.common.CoreResult
import template.core.common.Location
import template.core.heresdk.HereRouteCallback
import template.core.heresdk.HereRouteResult
import template.core.heresdk.HereSdkBridgeHolder
import template.core.map.route.api.MapRouteRepository
import template.core.map.route.api.Route
import kotlin.coroutines.resume

@Factory
internal class HereMapRouteRepositoryIos : MapRouteRepository {
    override suspend fun getCarRoute(
        from: Location,
        to: Location,
    ): CoreResult<Route> =
        suspendCancellableCoroutine { cont ->
            val bridge = HereSdkBridgeHolder.routeBridge
            if (bridge == null) {
                cont.resume(CoreResult.Failure(Error("HERE SDK not initialized")))
                return@suspendCancellableCoroutine
            }
            bridge.calculateCarRoute(
                fromLat = from.lat,
                fromLon = from.lon,
                toLat = to.lat,
                toLon = to.lon,
                callback =
                    object : HereRouteCallback {
                        override fun onSuccess(result: HereRouteResult) {
                            cont.resume(
                                CoreResult.Success(
                                    Route(
                                        polyline = result.polyline.map { Location(it.lat, it.lon) },
                                        durationSeconds = result.durationSeconds,
                                        distanceMeters = result.distanceMeters,
                                    ),
                                ),
                            )
                        }

                        override fun onError(message: String) {
                            cont.resume(CoreResult.Failure(Error(message)))
                        }
                    },
            )
        }
}
