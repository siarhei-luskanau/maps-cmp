package template.ui.maps.view.here

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.core.annotation.Factory
import template.ui.maps.view.api.MapsConfig
import template.ui.maps.view.api.MapsViewProvider

@Factory
class HereMapsViewProvider : MapsViewProvider {
    @Composable
    override fun MapsView(
        config: MapsConfig,
        modifier: Modifier,
    ) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Map view stub")
        }
    }
}
