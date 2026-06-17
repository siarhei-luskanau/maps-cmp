package template.core.pref

import org.koin.core.annotation.KoinApplication
import template.core.common.CoreCommonCommonModule

@KoinApplication(
    modules = [
        CoreCommonCommonModule::class,
        CorePrefCommonModule::class,
    ],
)
internal class TestKoinApplication
