package template.ui.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.StateFlow

@Composable
fun SplashScreen(viewModelProvider: () -> SplashViewModel) {
    val viewModel = viewModel { viewModelProvider() }
    SplashContent(
        viewStateFlow = viewModel.viewState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
internal fun SplashContent(
    viewStateFlow: StateFlow<SplashViewState>,
    onEvent: (SplashViewEvent) -> Unit,
) {
    val viewState = viewStateFlow.collectAsState().value
    Scaffold { contentPadding ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            if (viewState == SplashViewState.Loading) {
                CircularProgressIndicator()
            }
        }
    }
    LaunchedEffect(Unit) {
        onEvent(SplashViewEvent.Launched)
    }
}
