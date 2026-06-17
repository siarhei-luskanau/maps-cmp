package template.core.pref

import android.content.Context
import kotlinx.coroutines.runBlocking
import okio.Path
import okio.Path.Companion.toPath
import org.koin.core.annotation.Single
import template.core.common.DispatcherSet

@Single
internal class AppPrefPathProviderAndroid(
    private val context: Context,
    private val dispatcherSet: DispatcherSet,
) : PrefPathProvider {
    override fun get(): Path =
        runBlocking(dispatcherSet.ioDispatcher()) {
            val file = context.filesDir.resolve("app.pref.json")
            file.absolutePath.toPath()
        }
}
