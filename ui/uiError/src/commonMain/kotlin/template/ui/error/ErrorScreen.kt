package template.ui.error

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import template.ui.common.theme.AppTheme

@Composable
fun ErrorScreen(state: ErrorViewState) {
    ErrorContent(state = state)
}

@Composable
internal fun ErrorContent(state: ErrorViewState) {
    Scaffold { contentPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Invalid API Key",
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = state.message,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Preview
@Composable
internal fun ErrorScreenPreview() =
    AppTheme {
        ErrorContent(state = ErrorViewState(message = "Access key is invalid. Please check local.properties."))
    }
