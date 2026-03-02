package template.core.heresdk

interface HereRouteBridge {
    fun calculateCarRoute(
        fromLat: Double,
        fromLon: Double,
        toLat: Double,
        toLon: Double,
        callback: HereRouteCallback,
    )
}

interface HereRouteCallback {
    fun onSuccess(result: HereRouteResult)

    fun onError(message: String)
}

data class HereRouteResult(
    val polyline: List<HereMapsPoint>,
    val durationSeconds: Long,
    val distanceMeters: Long,
)
