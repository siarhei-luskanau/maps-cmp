package template.ui.map.route

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.github.takahirom.roborazzi.captureRoboImage
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
internal class MapRouteScreenJvmTest {
    @Test
    fun preview() =
        runDesktopComposeUiTest {
            setContent { MapRouteScreenPreview() }
            onRoot().captureRoboImage()
        }
}
