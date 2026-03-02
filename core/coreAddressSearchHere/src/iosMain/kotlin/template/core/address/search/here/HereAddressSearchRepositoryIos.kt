package template.core.address.search.here

import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.core.annotation.Factory
import template.core.address.search.api.AddressItem
import template.core.address.search.api.AddressSearchRepository
import template.core.common.CoreResult
import template.core.heresdk.HereSdkBridgeHolder
import template.core.heresdk.HereSearchCallback
import template.core.heresdk.HereSearchResult
import kotlin.coroutines.resume

@Factory
internal class HereAddressSearchRepositoryIos : AddressSearchRepository {
    override suspend fun search(query: String): CoreResult<List<AddressItem>> =
        suspendCancellableCoroutine { cont ->
            val bridge = HereSdkBridgeHolder.searchBridge
            if (bridge == null) {
                cont.resume(CoreResult.Failure(Error("HERE SDK not initialized")))
                return@suspendCancellableCoroutine
            }
            bridge.searchByAddress(
                query = query,
                callback =
                    object : HereSearchCallback {
                        override fun onSuccess(results: List<HereSearchResult>) {
                            cont.resume(
                                CoreResult.Success(
                                    results.map { r ->
                                        AddressItem(
                                            id = r.placeId,
                                            label = r.label,
                                            lat = r.latitude,
                                            lon = r.longitude,
                                        )
                                    },
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
