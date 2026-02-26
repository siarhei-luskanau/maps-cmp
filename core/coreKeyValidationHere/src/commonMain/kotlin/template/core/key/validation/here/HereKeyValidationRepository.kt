package template.core.key.validation.here

import org.koin.core.annotation.Factory
import template.core.key.validation.api.HereCredentials
import template.core.key.validation.api.KeyValidationRepository
import template.core.key.validation.api.KeyValidationResult

@Factory
class HereKeyValidationRepository : KeyValidationRepository {
    override suspend fun validateCredentials(credentials: HereCredentials): KeyValidationResult = KeyValidationResult.Valid
}
