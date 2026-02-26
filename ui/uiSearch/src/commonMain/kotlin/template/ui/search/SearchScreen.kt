package template.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import template.core.address.search.api.AddressItem
import template.ui.common.theme.AppTheme

@Composable
fun SearchScreen(viewModelProvider: () -> SearchViewModel) {
    val viewModel = viewModel { viewModelProvider() }
    SearchContent(
        viewStateFlow = viewModel.viewState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
internal fun SearchContent(
    viewStateFlow: StateFlow<SearchViewState>,
    onEvent: (SearchViewEvent) -> Unit,
) {
    val viewState = viewStateFlow.collectAsState().value
    Scaffold { contentPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(horizontal = 16.dp),
        ) {
            when (val mode = viewState.departureMode) {
                DepartureMode.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                }

                DepartureMode.CurrentLocation -> {
                    OutlinedTextField(
                        value = "Current Location",
                        onValueChange = {},
                        label = { Text("Departure") },
                        enabled = false,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                    )
                }

                DepartureMode.ManualEntry -> {
                    OutlinedTextField(
                        value = viewState.departureQuery,
                        onValueChange = { onEvent(SearchViewEvent.QueryChanged(SearchField.Departure, it)) },
                        label = { Text("Departure") },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .clickable { onEvent(SearchViewEvent.FieldFocused(SearchField.Departure)) },
                    )
                }
            }
            OutlinedTextField(
                value = viewState.destinationQuery,
                onValueChange = { onEvent(SearchViewEvent.QueryChanged(SearchField.Destination, it)) },
                label = { Text("Destination") },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clickable { onEvent(SearchViewEvent.FieldFocused(SearchField.Destination)) },
            )
            if (viewState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            }
            viewState.error?.let { error ->
                Text(text = error, modifier = Modifier.padding(top = 8.dp))
            }
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(viewState.results, key = { it.id }) { item ->
                    ListItem(
                        headlineContent = { Text(item.label) },
                        modifier =
                            Modifier.clickable {
                                onEvent(SearchViewEvent.AddressSelected(viewState.activeField, item))
                            },
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Preview
@Composable
internal fun SearchScreenPreview() =
    AppTheme {
        SearchContent(
            viewStateFlow =
                MutableStateFlow(
                    SearchViewState(
                        departureMode = DepartureMode.CurrentLocation,
                        activeField = SearchField.Destination,
                        destinationQuery = "Berlin",
                        results =
                            listOf(
                                AddressItem(id = "1", label = "Berlin, Germany", lat = 52.5, lon = 13.4),
                                AddressItem(id = "2", label = "Berlin Mitte, Germany", lat = 52.52, lon = 13.41),
                            ),
                    ),
                ),
            onEvent = {},
        )
    }
