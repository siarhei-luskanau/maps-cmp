package template.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import template.core.address.search.api.AddressItem
import template.core.address.search.api.AddressSearchRepository
import template.core.common.CoreResult

@Factory
class SearchViewModel(
    @InjectedParam private val navigationCallback: SearchNavigationCallback,
    private val addressSearchRepository: AddressSearchRepository,
) : ViewModel() {
    val viewState: StateFlow<SearchViewState>
        field = MutableStateFlow(SearchViewState())

    init {
        observeQueryChanges()
    }

    fun onEvent(event: SearchViewEvent) {
        when (event) {
            is SearchViewEvent.FieldFocused -> {
                viewState.value = viewState.value.copy(activeField = event.field, results = emptyList())
            }

            is SearchViewEvent.QueryChanged -> {
                when (event.field) {
                    SearchField.Departure -> viewState.value = viewState.value.copy(departureQuery = event.query)
                    SearchField.Destination -> viewState.value = viewState.value.copy(destinationQuery = event.query)
                }
            }

            is SearchViewEvent.AddressSelected -> {
                onAddressSelected(event.field, event.item)
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeQueryChanges() {
        viewModelScope.launch {
            viewState
                .map { state ->
                    when (state.activeField) {
                        SearchField.Departure -> state.departureQuery
                        SearchField.Destination -> state.destinationQuery
                    }
                }.drop(1)
                .distinctUntilChanged()
                .debounce(500L)
                .collect { query ->
                    if (query.isBlank()) {
                        viewState.value = viewState.value.copy(results = emptyList())
                        return@collect
                    }
                    viewState.value = viewState.value.copy(isLoading = true, error = null)
                    when (val result = addressSearchRepository.search(query)) {
                        is CoreResult.Success -> viewState.value = viewState.value.copy(results = result.result, isLoading = false)
                        is CoreResult.Failure -> viewState.value = viewState.value.copy(error = result.error.message, isLoading = false)
                    }
                }
        }
    }

    private fun onAddressSelected(
        field: SearchField,
        item: AddressItem,
    ) {
        when (field) {
            SearchField.Departure -> {
                viewState.value =
                    viewState.value.copy(
                        departureSelection = item,
                        departureQuery = item.label,
                        activeField = SearchField.Destination,
                        results = emptyList(),
                    )
            }

            SearchField.Destination -> {
                viewState.value =
                    viewState.value.copy(
                        destinationSelection = item,
                        destinationQuery = item.label,
                        results = emptyList(),
                    )
                val departure = viewState.value.departureSelection
                if (departure != null) {
                    navigationCallback.goMapRoute(departure = departure, destination = item)
                }
            }
        }
    }
}
