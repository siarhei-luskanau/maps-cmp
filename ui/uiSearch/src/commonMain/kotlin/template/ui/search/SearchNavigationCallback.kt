package template.ui.search

import template.core.address.search.api.AddressItem

interface SearchNavigationCallback {
    fun goMapRoute(
        departure: AddressItem?,
        destination: AddressItem,
    )
}
