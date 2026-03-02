package template.core.location.api

import kotlinx.coroutines.flow.Flow
import template.core.common.Location

interface LocationRepository {
    fun locationFlow(): Flow<Location?>

    suspend fun requestPermission(): LocationPermissionResult
}
