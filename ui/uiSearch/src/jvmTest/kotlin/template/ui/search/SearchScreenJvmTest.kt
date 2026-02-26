package template.ui.search

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.github.takahirom.roborazzi.captureRoboImage
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
internal class SearchScreenJvmTest {
    @Test
    fun preview() =
        runDesktopComposeUiTest {
            setContent { SearchScreenPreview() }
            onRoot().captureRoboImage()
        }
}
