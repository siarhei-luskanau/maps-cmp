package template.ui.maps.view.here

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.here.sdk.core.Color
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.core.GeoPolyline
import com.here.sdk.core.engine.AuthenticationMode
import com.here.sdk.core.engine.SDKNativeEngine
import com.here.sdk.core.engine.SDKOptions
import com.here.sdk.core.errors.InstantiationErrorException
import com.here.sdk.mapview.LineCap
import com.here.sdk.mapview.MapImageFactory
import com.here.sdk.mapview.MapMarker
import com.here.sdk.mapview.MapMeasure
import com.here.sdk.mapview.MapMeasureDependentRenderSize
import com.here.sdk.mapview.MapPolyline
import com.here.sdk.mapview.MapScheme
import com.here.sdk.mapview.MapView
import com.here.sdk.mapview.RenderSize
import com.here.sdk.units.compass.CompassUnit
import com.here.sdk.units.compass.CompassView
import com.here.sdk.units.mapruler.MapScaleUnit
import com.here.sdk.units.mapruler.MapScaleView
import com.here.sdk.units.mapswitcher.MapSwitcherUnit
import com.here.sdk.units.mapswitcher.MapSwitcherView
import org.koin.core.annotation.Factory
import template.core.heresdk.BuildConfig
import template.ui.maps.view.api.MapsConfig
import template.ui.maps.view.api.MapsViewProvider

@Factory
internal class HereMapsViewProviderAndroid : MapsViewProvider {
    @Composable
    override fun MapsView(
        config: MapsConfig,
        modifier: Modifier,
    ) {
        val mapSwitcherUnit = remember { MapSwitcherUnit() }
        val compassUnit = remember { CompassUnit() }
        val mapScaleUnit = remember { MapScaleUnit() }
        Box(modifier = modifier) {
            HereMapView(config, mapSwitcherUnit, compassUnit, mapScaleUnit)
            MapOverlayLayout(mapSwitcherUnit, compassUnit, mapScaleUnit)
        }
    }

    @Composable
    private fun HereMapView(
        config: MapsConfig,
        mapSwitcherUnit: MapSwitcherUnit,
        compassUnit: CompassUnit,
        mapScaleUnit: MapScaleUnit,
    ) {
        var mapView: MapView? = remember { null }
        val lifecycle = (LocalContext.current as LifecycleOwner).lifecycle
        DisposableEffect(lifecycle) {
            val observer =
                object : DefaultLifecycleObserver {
                    override fun onResume(owner: LifecycleOwner) {
                        mapView?.onResume()
                    }

                    override fun onPause(owner: LifecycleOwner) {
                        mapView?.onPause()
                    }

                    override fun onDestroy(owner: LifecycleOwner) {
                        mapView?.onDestroy()
                    }
                }
            lifecycle.addObserver(observer)
            onDispose { lifecycle.removeObserver(observer) }
        }
        AndroidView(
            factory = { context ->
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
                mapView =
                    MapView(context).also {
                        it.onCreate(null)
                        mapSwitcherUnit.setUp(it)
                        compassUnit.setUp(it)
                        mapScaleUnit.setUp(it)
                    }
                mapView.setOnReadyListener {
                    mapSwitcherUnit.setUp(mapView)
                    compassUnit.setUp(mapView)
                    mapScaleUnit.setUp(mapView)
                    mapView.mapScene.loadScene(MapScheme.NORMAL_DAY) { mapError ->
                        if (mapError != null) return@loadScene
                        mapView.camera.lookAt(
                            GeoCoordinates(config.center.lat, config.center.lon),
                            MapMeasure(MapMeasure.Kind.ZOOM_LEVEL, config.zoom.toDouble()),
                        )
                        config.route?.let { route ->
                            if (route.polyline.size >= 2) {
                                showRoutePolyline(
                                    mapView,
                                    route.polyline.map { GeoCoordinates(it.lat, it.lon) },
                                )
                            }
                        }
                        config.currentLocation?.let { location ->
                            showLocationMarker(mapView, GeoCoordinates(location.lat, location.lon))
                        }
                    }
                }
                mapView
            },
            modifier = Modifier.fillMaxSize(),
        )
    }

    @Composable
    private fun MapOverlayLayout(
        mapSwitcherUnit: MapSwitcherUnit,
        compassUnit: CompassUnit,
        mapScaleUnit: MapScaleUnit,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            MapScaleView(
                unit = mapScaleUnit,
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
            )
            CompassView(
                unit = compassUnit,
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
            )
            MapSwitcherView(
                unit = mapSwitcherUnit,
                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
            )
        }
    }

    private fun showRoutePolyline(
        mapView: MapView,
        coordinates: List<GeoCoordinates>,
    ) {
        val geoPolyline =
            try {
                GeoPolyline(coordinates)
            } catch (e: InstantiationErrorException) {
                return
            }
        val polyline =
            try {
                MapPolyline(
                    geoPolyline,
                    MapPolyline.SolidRepresentation(
                        MapMeasureDependentRenderSize(RenderSize.Unit.PIXELS, 12.0),
                        Color(0f, 0.4f, 1f, 1f),
                        LineCap.ROUND,
                    ),
                )
            } catch (e: MapPolyline.Representation.InstantiationException) {
                return
            } catch (e: MapMeasureDependentRenderSize.InstantiationException) {
                return
            }
        mapView.mapScene.addMapPolyline(polyline)
    }

    private fun showLocationMarker(
        mapView: MapView,
        coords: GeoCoordinates,
    ) {
        val bitmap = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888)
        Canvas(bitmap).drawCircle(
            16f,
            16f,
            14f,
            Paint().apply {
                color = android.graphics.Color.BLUE
                isAntiAlias = true
            },
        )
        mapView.mapScene.addMapMarker(MapMarker(coords, MapImageFactory.fromBitmap(bitmap)))
    }
}
