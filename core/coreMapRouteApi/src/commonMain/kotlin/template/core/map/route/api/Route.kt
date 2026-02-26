package template.core.map.route.api

data class Route(
    val polyline: List<Location>,
    val durationSeconds: Long,
    val distanceMeters: Long,
)
