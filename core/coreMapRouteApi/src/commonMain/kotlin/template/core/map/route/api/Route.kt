package template.core.map.route.api

import template.core.common.Location

data class Route(
    val polyline: List<Location>,
    val durationSeconds: Long,
    val distanceMeters: Long,
)
