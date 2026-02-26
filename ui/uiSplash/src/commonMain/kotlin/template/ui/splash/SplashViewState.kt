package template.ui.splash

sealed interface SplashViewState {
    data object Loading : SplashViewState

    data object Valid : SplashViewState

    data class Invalid(
        val message: String,
    ) : SplashViewState
}
