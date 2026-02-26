package template.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import template.core.key.validation.api.HereCredentials
import template.di.app.BuildConfig

@Module
@ComponentScan(value = ["template.di"])
class DiCommonModule {
    @Single
    fun provideHereCredentials(): HereCredentials =
        HereCredentials(
            accessKeyId = BuildConfig.HERE_ACCESS_KEY_ID,
            accessKeySecret = BuildConfig.HERE_ACCESS_KEY_SECRET,
        )
}
