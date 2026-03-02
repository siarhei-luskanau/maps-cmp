package template.core.address.search.here

import android.content.Context
import com.here.sdk.core.engine.AuthenticationMode
import com.here.sdk.core.engine.SDKNativeEngine
import com.here.sdk.core.engine.SDKOptions
import com.here.sdk.core.errors.InstantiationErrorException
import com.here.sdk.search.AddressQuery
import com.here.sdk.search.SearchEngine
import com.here.sdk.search.SearchOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.core.annotation.Factory
import template.core.address.search.api.AddressItem
import template.core.address.search.api.AddressSearchRepository
import template.core.common.CoreResult
import template.core.heresdk.BuildConfig
import kotlin.coroutines.resume

@Factory
internal class HereAddressSearchRepositoryAndroid(
    private val context: Context,
) : AddressSearchRepository {
    override suspend fun search(query: String): CoreResult<List<AddressItem>> =
        suspendCancellableCoroutine { cont ->
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
            val searchEngine =
                try {
                    SearchEngine()
                } catch (e: InstantiationErrorException) {
                    cont.resume(CoreResult.Success(emptyList()))
                    return@suspendCancellableCoroutine
                }
            searchEngine.searchByAddress(
                AddressQuery(query),
                SearchOptions().apply { maxItems = 20 },
            ) { searchError, places ->
                when {
                    searchError != null -> {
                        cont.resume(CoreResult.Failure(Error(searchError.name)))
                    }

                    else -> {
                        cont.resume(
                            CoreResult.Success(
                                places.orEmpty().mapNotNull { place ->
                                    val coords = place.geoCoordinates ?: return@mapNotNull null
                                    AddressItem(
                                        id = place.id,
                                        label = place.address.addressText.ifBlank { place.title },
                                        lat = coords.latitude,
                                        lon = coords.longitude,
                                    )
                                },
                            ),
                        )
                    }
                }
            }
        }
}
