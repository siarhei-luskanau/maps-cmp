package template.ui.search

import template.core.address.search.api.AddressItem

data class SearchViewState(
    val activeField: SearchField = SearchField.Departure,
    val departureQuery: String = "",
    val departureSelection: AddressItem? = null,
    val destinationQuery: String = "",
    val destinationSelection: AddressItem? = null,
    val results: List<AddressItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)
