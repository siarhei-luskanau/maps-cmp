package template.core.pref

import okio.Path

internal interface PrefPathProvider {
    fun get(): Path
}
