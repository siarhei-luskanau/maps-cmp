package template.ui.maps.view.here

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import org.koin.core.annotation.Factory
import platform.UIKit.UIView
import template.core.heresdk.HereMapsPoint
import template.core.heresdk.HereSdkBridgeHolder
import template.ui.maps.view.api.MapsConfig
import template.ui.maps.view.api.MapsViewProvider

@Factory
internal class HereMapsViewProviderIos : MapsViewProvider {
    @Composable
    override fun MapsView(
        config: MapsConfig,
        modifier: Modifier,
    ) {
        val bridge = HereSdkBridgeHolder.mapsViewBridge
        if (bridge == null) {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Map view not available")
            }
            return
        }
        UIKitView<UIView>(
            factory = {
                bridge.createMapView(
                    lat = config.center.lat,
                    lon = config.center.lon,
                    zoom = config.zoom,
                    polyline =
                        config.route?.polyline?.map { HereMapsPoint(it.lat, it.lon) }
                            ?: emptyList(),
                    hasLocation = config.currentLocation != null,
                    locationLat = config.currentLocation?.lat ?: 0.0,
                    locationLon = config.currentLocation?.lon ?: 0.0,
                )
            },
            modifier = modifier.fillMaxSize(),
        )
    }
}
