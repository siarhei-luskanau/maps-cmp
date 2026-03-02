package template.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import template.core.common.CoreResult
import template.core.key.validation.api.KeyValidationRepository

@Factory
class SplashViewModel(
    @InjectedParam private val navigationCallback: SplashNavigationCallback,
    private val keyValidationRepository: KeyValidationRepository,
) : ViewModel() {
    val viewState: StateFlow<SplashViewState>
        field = MutableStateFlow<SplashViewState>(SplashViewState.Loading)

    fun onEvent(event: SplashViewEvent) {
        when (event) {
            SplashViewEvent.Launched -> validate()
        }
    }

    private fun validate() {
        viewModelScope.launch {
            viewState.value = SplashViewState.Loading
            when (val result = keyValidationRepository.validateCredentials()) {
                is CoreResult.Success -> {
                    viewState.value = SplashViewState.Valid
                    navigationCallback.goSearchAddress()
                }

                is CoreResult.Failure -> {
                    val message = result.error.message ?: ""
                    viewState.value = SplashViewState.Invalid(message)
                    navigationCallback.goError(message)
                }
            }
        }
    }
}
