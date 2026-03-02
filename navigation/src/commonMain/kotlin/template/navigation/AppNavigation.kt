package template.navigation

import androidx.navigation3.runtime.NavKey
import template.core.address.search.api.AddressItem
import template.ui.map.route.MapRouteNavigationCallback
import template.ui.search.SearchNavigationCallback
import template.ui.splash.SplashNavigationCallback

internal class AppNavigation(
    private val backStack: MutableList<NavKey>,
) : SplashNavigationCallback,
    SearchNavigationCallback,
    MapRouteNavigationCallback {
    override fun goSearchAddress() {
        backStack.add(AppRoutes.SearchAddress)
        backStack.remove(AppRoutes.Splash)
    }

    override fun goError(message: String) {
        backStack.add(AppRoutes.Error(message = message))
        backStack.remove(AppRoutes.Splash)
    }

    override fun goMapRoute(
        departure: AddressItem,
        destination: AddressItem,
    ) {
        backStack.add(
            AppRoutes.MapRoute(
                departureLat = departure.lat,
                departureLon = departure.lon,
                destinationLat = destination.lat,
                destinationLon = destination.lon,
            ),
        )
    }

    override fun goBack() {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }
}
