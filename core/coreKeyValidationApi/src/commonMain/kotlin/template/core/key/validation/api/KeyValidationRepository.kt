package template.core.key.validation.api

interface KeyValidationRepository {
    suspend fun validateCredentials(credentials: HereCredentials): KeyValidationResult
}
