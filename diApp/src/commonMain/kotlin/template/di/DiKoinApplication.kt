package template.di

import org.koin.core.annotation.KoinApplication
import template.core.address.search.api.CoreAddressSearchApiCommonModule
import template.core.address.search.here.CoreAddressSearchHereCommonModule
import template.core.common.CoreCommonCommonModule
import template.core.key.validation.api.CoreKeyValidationApiCommonModule
import template.core.key.validation.here.CoreKeyValidationHereCommonModule
import template.core.location.api.CoreLocationApiCommonModule
import template.core.location.platform.CoreLocationPlatformCommonModule
import template.core.map.route.api.CoreMapRouteApiCommonModule
import template.core.map.route.here.CoreMapRouteHereCommonModule
import template.core.pref.CorePrefCommonModule
import template.ui.error.ErrorCommonModule
import template.ui.map.route.MapRouteCommonModule
import template.ui.maps.view.api.UiMapsViewApiCommonModule
import template.ui.maps.view.here.UiMapsViewHereCommonModule
import template.ui.search.SearchCommonModule
import template.ui.splash.SplashCommonModule

@KoinApplication(
    modules = [
        CoreCommonCommonModule::class,
        CorePrefCommonModule::class,
        CoreKeyValidationApiCommonModule::class,
        CoreKeyValidationHereCommonModule::class,
        CoreAddressSearchApiCommonModule::class,
        CoreAddressSearchHereCommonModule::class,
        CoreMapRouteApiCommonModule::class,
        CoreMapRouteHereCommonModule::class,
        CoreLocationApiCommonModule::class,
        CoreLocationPlatformCommonModule::class,
        DiCommonModule::class,
        UiMapsViewApiCommonModule::class,
        UiMapsViewHereCommonModule::class,
        ErrorCommonModule::class,
        SearchCommonModule::class,
        MapRouteCommonModule::class,
        SplashCommonModule::class,
    ],
)
internal class DiKoinApplication
