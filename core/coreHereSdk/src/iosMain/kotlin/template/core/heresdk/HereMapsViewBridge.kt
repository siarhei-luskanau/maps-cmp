package template.core.heresdk

import platform.UIKit.UIView

interface HereMapsViewBridge {
    fun createMapView(
        lat: Double,
        lon: Double,
        zoom: Float,
        polyline: List<HereMapsPoint>,
        hasLocation: Boolean,
        locationLat: Double,
        locationLon: Double,
    ): UIView
}

data class HereMapsPoint(
    val lat: Double,
    val lon: Double,
)
