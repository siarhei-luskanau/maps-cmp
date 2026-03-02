package template.core.key.validation.here

import org.koin.core.annotation.Factory
import template.core.common.CoreResult
import template.core.key.validation.api.KeyValidationRepository

@Factory
internal class HereKeyValidationRepositoryIos : KeyValidationRepository {
    override suspend fun validateCredentials(): CoreResult<Unit> = CoreResult.Success(Unit)
}
