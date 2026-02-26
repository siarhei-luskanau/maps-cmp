package template.core.key.validation.api

sealed interface KeyValidationResult {
    data object Valid : KeyValidationResult

    data class Invalid(
        val message: String,
    ) : KeyValidationResult
}
