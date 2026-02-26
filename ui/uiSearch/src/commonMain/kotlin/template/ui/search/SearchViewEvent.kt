package template.ui.search

import template.core.address.search.api.AddressItem

sealed interface SearchViewEvent {
    data class FieldFocused(
        val field: SearchField,
    ) : SearchViewEvent

    data class QueryChanged(
        val field: SearchField,
        val query: String,
    ) : SearchViewEvent

    data class AddressSelected(
        val field: SearchField,
        val item: AddressItem,
    ) : SearchViewEvent
}
