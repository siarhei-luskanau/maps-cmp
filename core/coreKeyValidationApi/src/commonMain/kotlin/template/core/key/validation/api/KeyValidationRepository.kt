package template.core.key.validation.api

import template.core.common.CoreResult

interface KeyValidationRepository {
    suspend fun validateCredentials(): CoreResult<Unit>
}
