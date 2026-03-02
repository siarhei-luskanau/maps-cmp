package template.core.heresdk

interface HereSearchBridge {
    fun searchByAddress(
        query: String,
        callback: HereSearchCallback,
    )
}

interface HereSearchCallback {
    fun onSuccess(results: List<HereSearchResult>)

    fun onError(message: String)
}

data class HereSearchResult(
    val placeId: String,
    val label: String,
    val latitude: Double,
    val longitude: Double,
)

object HereSdkBridgeHolder {
    var searchBridge: HereSearchBridge? = null
    var mapsViewBridge: HereMapsViewBridge? = null
    var routeBridge: HereRouteBridge? = null
}
