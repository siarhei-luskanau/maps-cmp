package template.ui.maps.view.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface MapsViewProvider {
    @Composable
    fun MapsView(
        config: MapsConfig,
        modifier: Modifier,
    )
}
