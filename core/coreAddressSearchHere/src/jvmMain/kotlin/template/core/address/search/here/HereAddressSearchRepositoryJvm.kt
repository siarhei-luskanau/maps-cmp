package template.core.address.search.here

import org.koin.core.annotation.Factory
import template.core.address.search.api.AddressItem
import template.core.address.search.api.AddressSearchRepository

@Factory
internal class HereAddressSearchRepositoryJvm : AddressSearchRepository {
    override suspend fun search(query: String): List<AddressItem> = emptyList()
}
