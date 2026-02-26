package template.core.address.search.api

interface AddressSearchRepository {
    suspend fun search(query: String): List<AddressItem>
}
