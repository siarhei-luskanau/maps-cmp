package template.core.key.validation.here

import org.koin.core.annotation.Factory
import template.core.key.validation.api.KeyValidationRepository
import template.core.key.validation.api.KeyValidationResult

@Factory
class KeyValidationRepositoryStub : KeyValidationRepository {
    override suspend fun validateCredentials(): KeyValidationResult = KeyValidationResult.Valid
}
