package template.core.address.search.api

import template.core.common.CoreResult

interface AddressSearchRepository {
    suspend fun search(query: String): CoreResult<List<AddressItem>>
}
