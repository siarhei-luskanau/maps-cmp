package template.core.location.platform

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.koin.core.annotation.Factory
import template.core.common.Location
import template.core.location.api.LocationPermissionResult
import template.core.location.api.LocationRepository

@Factory
class NativeLocationRepository : LocationRepository {
    override fun locationFlow(): Flow<Location?> = flowOf(null)

    override suspend fun requestPermission(): LocationPermissionResult = LocationPermissionResult.Granted
}
