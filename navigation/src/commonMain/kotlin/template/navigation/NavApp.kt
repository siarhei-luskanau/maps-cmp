package template.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable
import org.koin.compose.getKoin
import org.koin.core.parameter.parametersOf
import template.ui.common.theme.AppTheme
import template.ui.error.ErrorScreen
import template.ui.error.ErrorViewState
import template.ui.map.route.MapRouteScreen
import template.ui.maps.view.api.MapsViewProvider
import template.ui.search.SearchScreen
import template.ui.splash.SplashScreen

@Preview
@Composable
fun NavApp() =
    AppTheme {
        val koin = getKoin()
        val backStack = mutableStateListOf<NavKey>(AppRoutes.Splash)
        val appNavigation = AppNavigation(backStack = backStack)
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider =
                entryProvider {
                    entry<AppRoutes.Splash> {
                        SplashScreen {
                            koin.get { parametersOf(appNavigation) }
                        }
                    }
                    entry<AppRoutes.Error> { route ->
                        ErrorScreen(state = ErrorViewState(message = route.message))
                    }
                    entry<AppRoutes.SearchAddress> {
                        SearchScreen {
                            koin.get { parametersOf(appNavigation) }
                        }
                    }
                    entry<AppRoutes.MapRoute> { route ->
                        MapRouteScreen(
                            viewModelProvider = {
                                koin.get {
                                    parametersOf(
                                        appNavigation,
                                        route.departureLat,
                                        route.departureLon,
                                        route.destinationLat,
                                        route.destinationLon,
                                    )
                                }
                            },
                            mapsViewProvider = koin.get<MapsViewProvider>(),
                        )
                    }
                },
        )
    }

internal sealed interface AppRoutes : NavKey {
    @Serializable data object Splash : AppRoutes

    @Serializable data class Error(
        val message: String,
    ) : AppRoutes

    @Serializable data object SearchAddress : AppRoutes

    @Serializable data class MapRoute(
        val departureLat: Double,
        val departureLon: Double,
        val destinationLat: Double,
        val destinationLon: Double,
    ) : AppRoutes
}
