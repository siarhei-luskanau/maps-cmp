package template.ui.map.route

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import template.core.common.Location
import template.core.map.route.api.Route
import template.ui.common.resources.Res
import template.ui.common.resources.back_button
import template.ui.common.resources.ic_arrow_back
import template.ui.common.theme.AppTheme
import template.ui.maps.view.api.MapsConfig
import template.ui.maps.view.api.MapsViewProvider

@Composable
fun MapRouteScreen(
    viewModelProvider: () -> MapRouteViewModel,
    mapsViewProvider: MapsViewProvider,
) {
    val viewModel = viewModel { viewModelProvider() }
    MapRouteContent(
        viewStateFlow = viewModel.viewState,
        onEvent = viewModel::onEvent,
        mapsViewProvider = mapsViewProvider,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MapRouteContent(
    viewStateFlow: StateFlow<MapRouteViewState>,
    onEvent: (MapRouteViewEvent) -> Unit,
    mapsViewProvider: MapsViewProvider,
) {
    val viewState = viewStateFlow.collectAsState().value
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Route") },
                navigationIcon = {
                    IconButton(onClick = { onEvent(MapRouteViewEvent.NavigateBack) }) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.ic_arrow_back),
                            contentDescription = stringResource(Res.string.back_button),
                        )
                    }
                },
            )
        },
    ) { contentPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            contentAlignment = Alignment.Center,
        ) {
            when (val state = viewState) {
                MapRouteViewState.Loading -> {
                    CircularProgressIndicator()
                }

                is MapRouteViewState.Ready -> {
                    mapsViewProvider.MapsView(
                        config = state.config,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                is MapRouteViewState.Error -> {
                    androidx.compose.foundation.layout.Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = state.message)
                        Button(onClick = { onEvent(MapRouteViewEvent.RetryRoute) }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

private val previewMapsViewProvider =
    object : MapsViewProvider {
        @Composable
        override fun MapsView(
            config: MapsConfig,
            modifier: Modifier,
        ) {
            Box(modifier = modifier, contentAlignment = Alignment.Center) {
                Text("Map: ${config.center.lat}, ${config.center.lon}")
            }
        }
    }

@Preview
@Composable
internal fun MapRouteScreenPreview() =
    AppTheme {
        MapRouteContent(
            viewStateFlow =
                MutableStateFlow(
                    MapRouteViewState.Ready(
                        config =
                            MapsConfig(
                                center = Location(lat = 52.52, lon = 13.40),
                                currentLocation = Location(lat = 52.50, lon = 13.38),
                                route = Route(polyline = emptyList(), durationSeconds = 600, distanceMeters = 3200),
                            ),
                    ),
                ),
            onEvent = {},
            mapsViewProvider = previewMapsViewProvider,
        )
    }
