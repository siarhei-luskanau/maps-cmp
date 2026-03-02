package template.core.key.validation.here

import android.content.Context
import com.here.sdk.core.engine.AuthenticationMode
import com.here.sdk.core.engine.SDKNativeEngine
import com.here.sdk.core.engine.SDKOptions
import com.here.sdk.core.errors.InstantiationErrorException
import org.koin.core.annotation.Factory
import template.core.common.CoreResult
import template.core.heresdk.BuildConfig
import template.core.key.validation.api.KeyValidationRepository

@Factory
internal class HereKeyValidationRepositoryAndroid(
    private val context: Context,
) : KeyValidationRepository {
    override suspend fun validateCredentials(): CoreResult<Unit> =
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
            CoreResult.Success(Unit)
        } catch (e: InstantiationErrorException) {
            CoreResult.Failure(Exception(e.error.name))
        }
}
