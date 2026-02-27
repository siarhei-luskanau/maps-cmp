package template.core.key.validation.here

import android.content.Context
import com.here.sdk.core.engine.AuthenticationMode
import com.here.sdk.core.engine.SDKNativeEngine
import com.here.sdk.core.engine.SDKOptions
import com.here.sdk.core.errors.InstantiationErrorException
import org.koin.core.annotation.Factory
import template.core.heresdk.BuildConfig
import template.core.key.validation.api.KeyValidationRepository
import template.core.key.validation.api.KeyValidationResult

@Factory
internal class HereKeyValidationRepositoryAndroid(
    private val context: Context,
) : KeyValidationRepository {
    override suspend fun validateCredentials(): KeyValidationResult =
        try {
            if (SDKNativeEngine.getSharedInstance() == null) {
                SDKNativeEngine.makeSharedInstance(
                    context,
                    SDKOptions(
                        AuthenticationMode.withKeySecret(
                            BuildConfig.HERE_ACCESS_KEY_ID,
                            BuildConfig.HERE_ACCESS_KEY_SECRET,
                        ),
                    ),
                )
            }
            KeyValidationResult.Valid
        } catch (e: InstantiationErrorException) {
            KeyValidationResult.Invalid(e.error.name)
        }
}
