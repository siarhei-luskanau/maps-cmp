# Maps CMP — Project Plan

## Overview

A Kotlin Multiplatform (KMP) + Compose Multiplatform (CMP) maps application using the **HERE SDK for Android/iOS (Explore edition, v4.25.1.0)**.
Targets: **Android**, **iOS**, **Desktop** (Desktop gets a stub map view).

HERE SDK uses **two credentials** — `accessKeyID` and `accessKeySecret` — provided at build time via `local.properties`. There is no single "API key" string.

---

## Screen Flow

```
Splash ──[key valid]──▶ Search Address ──[address selected]──▶ Map Route
   │
   └──[key invalid]──▶ Error (dead end)
```

- **Splash**: reads `BuildConfig.HERE_ACCESS_KEY_ID` + `BuildConfig.HERE_ACCESS_KEY_SECRET`, calls `coreKeyValidationApi`, navigates accordingly.
- **Error**: shows error message only; user must fix the key and restart.
- **Search Address**: two text fields (departure + destination) + shared results list. Location permission is requested on entry. If granted, departure is locked to "Current Location". If denied, departure field becomes a manual text input where the user searches and selects an address. Navigation to Map Route triggers automatically once both fields are resolved.
- **Map Route**: HERE map + car route polyline. Departure is either live GPS (permission granted) or a fixed coordinate from the searched address (permission denied).

---

## Gradle Module Structure

### Existing modules (kept / adapted)

| Module | Role |
|--------|------|
| `:app:androidApp` | Android entry point |
| `:app:desktopApp` | Desktop entry point |
| `:app:iosApp` | Xcode project |
| `:buildSrc` | Shared build logic |
| `:convention-plugin-multiplatform` | Convention plugins |
| `:core:coreCommon` | `DispatcherSet`, `PlatformService` |
| `:core:corePref` | DataStore preferences |
| `:diApp` | Root Koin module wiring |
| `:navigation` | `AppNavigation`, `AppRoutes` |
| `:ui:uiCommon` | Theme, shared drawables/strings |
| `:ui:uiSplash` | Splash screen — **needs update** |
| `:ui:uiMain` | Host `NavApp` — **needs update** |

### New modules

| Module | Role |
|--------|------|
| `:core:coreKeyValidationApi` | `KeyValidationRepository` interface |
| `:core:coreKeyValidationHere` | HERE SDK `SDKNativeEngine` implementation |
| `:core:coreAddressSearchApi` | `AddressSearchRepository` interface + `AddressItem` |
| `:core:coreAddressSearchHere` | HERE Geocoding API implementation |
| `:core:coreMapRouteApi` | `MapRouteRepository` interface + `Route` |
| `:core:coreMapRouteHere` | HERE Routing v8 implementation |
| `:core:coreLocationApi` | `LocationRepository` interface + `Location` |
| `:core:coreLocationNative` | Android (FusedLocation) + iOS (CLLocationManager) + Desktop (stub) |
| `:ui:uiMapsViewApi` | `MapsViewProvider` interface + `MapsConfig` data |
| `:ui:uiMapsViewHere` | HERE SDK Composable implementation |
| `:ui:uiError` | Error screen |
| `:ui:uiSearch` | Search Address screen |
| `:ui:uiMapRoute` | Map + Route screen |

---

## Module Specifications

### `:core:coreKeyValidationApi`

```
commonMain only — no platform code
```

```kotlin
data class HereCredentials(val accessKeyId: String, val accessKeySecret: String)

interface KeyValidationRepository {
    suspend fun validateCredentials(credentials: HereCredentials): KeyValidationResult
}

sealed class KeyValidationResult {
    object Valid : KeyValidationResult()
    data class Invalid(val message: String) : KeyValidationResult()
}
```

### `:core:coreKeyValidationHere`

- Implements `KeyValidationRepository`
- Attempts `SDKNativeEngine.makeSharedInstance(context, SDKOptions(authenticationMode))` with the provided credentials; catches `InstantiationErrorException` → `Invalid`
- On success leaves the engine initialized (it is reused by all other HERE components) → `Valid`
- `expect/actual` for Android (needs `Context`) vs iOS/Desktop (no context needed)
- Koin module: `coreKeyValidationHereModule`

### `:core:coreAddressSearchApi`

```kotlin
data class AddressItem(
    val id: String,
    val label: String,
    val lat: Double,
    val lon: Double,
)

interface AddressSearchRepository {
    suspend fun search(query: String): List<AddressItem>
}
```

### `:core:coreAddressSearchHere`

- Uses HERE SDK `SearchEngine` (package `com.here.sdk.search` / `heresdk` module)
- Init: `SearchEngine()` — throws `InstantiationErrorException` on failure
- Call: `searchEngine.searchByAddress(AddressQuery(query), SearchOptions(...)) { error, places -> ... }`
- Result mapping: `Place.title` → `AddressItem.label`, `Place.geoCoordinates.latitude/longitude` → coords, `Place.address.addressText` for full label
- Koin module: `coreAddressSearchHereModule`

### `:core:coreMapRouteApi`

```kotlin
data class Location(val lat: Double, val lon: Double)

data class Route(
    val polyline: List<Location>,
    val durationSeconds: Long,
    val distanceMeters: Long,
)

interface MapRouteRepository {
    suspend fun getCarRoute(from: Location, to: Location): Route
}
```

### `:core:coreMapRouteHere`

- Uses HERE SDK `RoutingEngine` (package `com.here.sdk.routing` / `heresdk` module)
- Init: `RoutingEngine()` — throws `InstantiationErrorException` on failure
- Call: `routingEngine.calculateRoute(listOf(startWaypoint, destWaypoint), CarOptions(), callback)`
  - Android: `RoutingEngine().calculateRoute(waypoints, carOptions, CalculateRouteCallback)`
  - iOS: `routingEngine.calculateRoute(with: waypoints, carOptions: CarOptions()) { error, routes in ... }`
- Result: `routes.first().geometry` is `GeoPolyline`; access vertices via `route.geometry.vertices: List<GeoCoordinates>`
- `route.lengthInMeters: Int`, `route.duration: Duration` (Kotlin) / `TimeInterval` (Swift)
- No custom polyline encoding — SDK returns structured `GeoCoordinates` list directly
- Koin module: `coreMapRouteHereModule`

### `:core:coreLocationApi`

```kotlin
interface LocationRepository {
    fun locationFlow(): Flow<Location?>
    suspend fun requestPermission(): LocationPermissionResult
}

enum class LocationPermissionResult { Granted, Denied, PermanentlyDenied }
```

### `:core:coreLocationNative`

- **Android**: `FusedLocationProviderClient` wrapped in `callbackFlow`
- **iOS**: `CLLocationManager` delegate wrapped in `callbackFlow`
- **Desktop**: stub — `locationFlow()` emits `null`, permission always `Granted`
- Koin module: `coreLocationNativeModule`

### `:ui:uiMapsViewApi`

```kotlin
data class MapsConfig(
    val center: Location,
    val zoom: Float = 14f,
    val currentLocation: Location?,
    val route: Route?,
)

interface MapsViewProvider {
    @Composable
    fun MapsView(config: MapsConfig, modifier: Modifier)
}
```

No platform code — interface only.

### `:ui:uiMapsViewHere`

- **Android** (`androidMain`): `AndroidView { MapView(context).also { it.onCreate(null); it.onResume() } }`
  - MapView lifecycle must be forwarded: `onCreate`, `onResume`, `onPause`, `onDestroy`, `onSaveInstanceState`
  - `mapView.mapScene.loadScene(MapScheme.NORMAL_DAY) { mapError -> ... }`
  - `mapView.camera.lookAt(GeoCoordinates(lat, lon), MapMeasure(MapMeasure.Kind.DISTANCE_IN_METERS, 1000.0))`
- **iOS** (`iosMain`): `UIViewRepresentable` wrapping `MapView()` from `heresdk`
  - `mapView.mapScene.loadScene(mapScheme: .normalDay) { mapError in ... }`
  - `mapView.camera.lookAt(point: GeoCoordinates(...), zoom: MapMeasure(kind: .distanceInMeters, value: 1000))`
- **Desktop** (`jvmMain`): `Box` with "Map not available on Desktop" placeholder text
- Koin module: `uiMapsViewHereModule` providing `MapsViewProvider`

### `:ui:uiSplash` (update existing)

Replace `SplashNavigationCallback.goMainScreen(initArg)` with:
```kotlin
interface SplashNavigationCallback {
    fun goSearchAddress()
    fun goError(message: String)
}
```

`SplashViewState` — follow existing `sealed interface` pattern:
```kotlin
sealed interface SplashViewState {
    data object Loading : SplashViewState
    data object Valid : SplashViewState
    data class Invalid(val message: String) : SplashViewState
}
```

`SplashViewModel` calls `keyValidationRepository.validateCredentials(HereCredentials(...))` on `SplashViewEvent.Launched`; navigates on result.

### `:ui:uiError`

```kotlin
data class ErrorViewState(val message: String)

// No navigation callback (dead end)
@Composable fun ErrorScreen(state: ErrorViewState)
```

Route: `AppRoutes.Error(message: String)`

### `:ui:uiSearch`

```kotlin
enum class SearchField { Departure, Destination }

sealed class DepartureMode {
    object Loading : DepartureMode()          // permission request in progress
    object CurrentLocation : DepartureMode()  // permission granted — field locked
    object ManualEntry : DepartureMode()      // permission denied — field editable
}

data class SearchViewState(
    val departureMode: DepartureMode = DepartureMode.Loading,
    val activeField: SearchField = SearchField.Destination,
    val departureQuery: String = "",
    val departureSelection: AddressItem? = null,
    val destinationQuery: String = "",
    val destinationSelection: AddressItem? = null,
    val results: List<AddressItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed class SearchViewEvent {
    data class FieldFocused(val field: SearchField) : SearchViewEvent()
    data class QueryChanged(val field: SearchField, val query: String) : SearchViewEvent()
    data class AddressSelected(val field: SearchField, val item: AddressItem) : SearchViewEvent()
}

interface SearchNavigationCallback {
    fun goMapRoute(departure: AddressItem?, destination: AddressItem)
    // departure = null means "use live GPS location"
}
```

**ViewModel behavior:**
- On init: calls `locationRepository.requestPermission()` → sets `departureMode`
  - `Granted` → `CurrentLocation` (departure field locked, active field switches to `Destination`)
  - `Denied` / `PermanentlyDenied` → `ManualEntry` (departure field editable, active field is `Departure`)
- `QueryChanged`: debounces 500 ms, calls `AddressSearchRepository.search()`, updates `results`
- `AddressSelected(Departure, item)`: sets `departureSelection`, switches `activeField` to `Destination`, clears results
- `AddressSelected(Destination, item)`: sets `destinationSelection`; if departure is also resolved (either `CurrentLocation` or `departureSelection != null`) → calls `goMapRoute()`
- Results list is always for the currently active field

Route: `AppRoutes.SearchAddress`

### `:ui:uiMapRoute`

```kotlin
data class MapRouteViewState(
    val config: MapsConfig,
    val isLoadingRoute: Boolean = false,
    val error: String? = null,
)

sealed class MapRouteViewEvent {
    object RetryRoute : MapRouteViewEvent()
}

interface MapRouteNavigationCallback {
    fun goBack()
}
```

- Receives both departure and destination from navigation args
- If `departureLat/Lon` is null → ViewModel collects `locationFlow()` for live GPS as the `from` point
- If `departureLat/Lon` is set (manual entry) → uses fixed coordinates, no GPS needed
- Calls `getCarRoute(from, to)` once departure is resolved
- Route: `AppRoutes.MapRoute(departureLat?, departureLon?, destinationLat, destinationLon)`

---

## Navigation

Update `:navigation` module. The existing pattern uses `internal sealed interface` with `@Serializable` and `data object` (matching `NavApp.kt`). The `kotlinx-serialization` plugin is already applied on the `:navigation` module.

Replace `AppRoutes.Main` with the new screens:

```kotlin
// NavApp.kt — AppRoutes
internal sealed interface AppRoutes : NavKey {
    @Serializable data object Splash : AppRoutes
    @Serializable data class Error(val message: String) : AppRoutes
    @Serializable data object SearchAddress : AppRoutes
    @Serializable data class MapRoute(
        val departureLat: Double?,   // null = use live GPS
        val departureLon: Double?,   // null = use live GPS
        val destinationLat: Double,
        val destinationLon: Double,
    ) : AppRoutes
}
```

`AppNavigation` implements all screen navigation callbacks:
- `SplashNavigationCallback.goSearchAddress()`
- `SplashNavigationCallback.goError(message)`
- `SearchNavigationCallback.goMapRoute(departure: AddressItem?, destination: AddressItem)`
- `MapRouteNavigationCallback.goBack()`

`NavApp.kt` entryProvider gains entries for `AppRoutes.Error`, `AppRoutes.SearchAddress`, `AppRoutes.MapRoute`; removes `AppRoutes.Main` entry.

Remove `goMainScreen` / `AppRoutes.Main`.

---

## SDK Credentials Setup

HERE SDK requires **two separate credential values** — not a single API key.

The project already has `buildSrc/src/main/kotlin/LocalPropertiesUtils.kt` with two helper functions:
```kotlin
fun hereAccessKeyID(properties: () -> Properties): String   // checks System.getProperty first, then local.properties
fun hereAccessKeySecret(properties: () -> Properties): String
```

`local.properties` property names (camelCase — defined by the existing utility):
```
hereAccessKeyID=your_access_key_id
hereAccessKeySecret=your_access_key_secret
```

Both keys are also readable from JVM system properties (e.g. `-DhereAccessKeyID=...`), making CI injection straightforward without modifying `local.properties`.

`:app:androidApp/build.gradle.kts` — apply the `buildConfig` plugin (already in version catalog as `libs.plugins.buildConfig`) and use the existing helpers:
```kotlin
plugins {
    // existing plugins...
    alias(libs.plugins.buildConfig)
}

buildConfig {
    val localProps = { Properties().apply { load(rootProject.file("local.properties").inputStream()) } }
    buildConfigField("String", "HERE_ACCESS_KEY_ID",     "\"${hereAccessKeyID(localProps)}\"")
    buildConfigField("String", "HERE_ACCESS_KEY_SECRET", "\"${hereAccessKeySecret(localProps)}\"")
}
```

**Android init pattern** (verified from `HelloMapKotlin/MainActivity.kt`):
```kotlin
val authenticationMode = AuthenticationMode.withKeySecret(accessKeyID, accessKeySecret)
val options = SDKOptions(authenticationMode)
SDKNativeEngine.makeSharedInstance(context, options)   // throws InstantiationErrorException
```

**iOS init pattern** (verified from `HelloMapApp.swift`):
```swift
let authenticationMode = AuthenticationMode.withKeySecret(accessKeyId: id, accessKeySecret: secret)
let options = SDKOptions(authenticationMode: authenticationMode)
try SDKNativeEngine.makeSharedInstance(options: options)   // throws on failure
```

**iOS cleanup**: `SDKNativeEngine.sharedInstance = nil`
**Android cleanup**: `SDKNativeEngine.getSharedInstance()?.dispose(); SDKNativeEngine.setSharedInstance(null)`

Credentials are forwarded to KMP via `HereCredentials(accessKeyId, accessKeySecret)` data class; platform app modules read from `BuildConfig`

---

## DI Wiring (`:diApp`)

Add all new Koin modules:

```kotlin
val appModule = module {
    includes(
        coreCommonCommonModule,
        corePrefCommonModule,
        coreKeyValidationHereModule,
        coreAddressSearchHereModule,
        coreMapRouteHereModule,
        coreLocationNativeModule,
        uiMapsViewHereModule,
        uiSplashCommonModule,
        uiErrorCommonModule,
        uiSearchCommonModule,
        uiMapRouteCommonModule,
        uiMainCommonModule,
    )
}
```

`HereCredentials(accessKeyId, accessKeySecret)` is provided as a Koin single, read from `BuildConfig` (Android) or `Info.plist` (iOS) in the platform app module. All HERE SDK components receive it from DI.

---

## Key Dependencies (libs.versions.toml additions)

| Library | Usage |
|---------|-------|
| `org.jetbrains.kotlinx:kotlinx-coroutines-core` | Flows (already present) |
| `org.jetbrains.kotlinx:kotlinx-serialization-json` | Model deserialization |

**HERE SDK — local file distribution (not on Maven Central):**

- **Android**: Place `heresdk-explore-android-4.25.1.0.aar` in `:ui:uiMapsViewHere/libs/` (and any other module using it). Reference via:
  ```kotlin
  implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar", "*.jar"))))
  ```
  `minSdk` and `compileSdk` are inherited from `libs.versions.toml` (`build-android-minSdk = 30`, `build-android-compileSdk = 36`) via the `composeMultiplatformConvention` plugin — do not hardcode them.

- **iOS**: Place `heresdk.xcframework` in the Xcode project folder. In Xcode → General → Frameworks, Libraries and Embedded Content: add `heresdk.xcframework` as "Embed & Sign".

No Ktor HTTP client is needed for the Search/Routing/Validation features — the HERE SDK handles networking internally via its native engines. Ktor may still be needed if any non-HERE REST calls are required.

---

## Project Config Changes

### `settings.gradle.kts` — add all new modules

```kotlin
include(
    // existing...
    ":core:coreKeyValidationApi",
    ":core:coreKeyValidationHere",
    ":core:coreAddressSearchApi",
    ":core:coreAddressSearchHere",
    ":core:coreMapRouteApi",
    ":core:coreMapRouteHere",
    ":core:coreLocationApi",
    ":core:coreLocationNative",
    ":ui:uiMapsViewApi",
    ":ui:uiMapsViewHere",
    ":ui:uiError",
    ":ui:uiSearch",
    ":ui:uiMapRoute",
)
```

### HERE SDK `.aar` placement

The `.aar` is needed in every module that directly calls HERE SDK classes. Place it in a `libs/` folder within each such module (or share via a flat-dir repository declared in `settings.gradle.kts`):

| Module | HERE SDK classes used |
|--------|----------------------|
| `:core:coreKeyValidationHere` | `SDKNativeEngine`, `SDKOptions`, `AuthenticationMode` |
| `:core:coreAddressSearchHere` | `SearchEngine`, `AddressQuery`, `Place` |
| `:core:coreMapRouteHere` | `RoutingEngine`, `Waypoint`, `CarOptions`, `Route` |
| `:ui:uiMapsViewHere` | `MapView`, `MapScheme`, `MapMeasure`, `GeoCoordinates` |

Recommended: declare a single shared `flatDir` in `settings.gradle.kts` pointing to a root-level `sdkLibs/` folder, so all four modules share one copy:

```kotlin
// settings.gradle.kts — inside dependencyResolutionManagement { repositories { ... } }
flatDir { dirs(rootDir.resolve("sdkLibs").absolutePath) }
```

Then each module adds:
```kotlin
implementation(fileTree(mapOf("dir" to "${rootProject.projectDir}/sdkLibs", "include" to listOf("*.aar"))))
```

iOS `heresdk.xcframework` stays in the Xcode project folder as before.

### Platform targets (from `composeMultiplatformConvention`)

All KMP library modules use `id("composeMultiplatformConvention")` which sets:
- `androidLibrary` (source set: `androidMain`)
- `jvm()` — Desktop (source set: **`jvmMain`**, not `desktopMain`)
- `iosArm64()` + `iosSimulatorArm64()` — **no** `iosX64` (source set: `iosMain`)

Modules with HERE SDK native code only apply it in `androidMain` / `iosMain`; `jvmMain` always gets a stub.

---

## Implementation Phases

### Phase 1 — Core API interfaces & models
1. `:core:coreKeyValidationApi`
2. `:core:coreAddressSearchApi`
3. `:core:coreMapRouteApi`
4. `:core:coreLocationApi`
5. `:ui:uiMapsViewApi`

### Phase 2 — HERE implementations (can run in parallel)
- `:core:coreKeyValidationHere`
- `:core:coreAddressSearchHere`
- `:core:coreMapRouteHere`
- `:core:coreLocationNative`
- `:ui:uiMapsViewHere`

### Phase 3 — UI screens (can run in parallel)
- `:ui:uiError`
- `:ui:uiSearch`
- `:ui:uiMapRoute`
- Update `:ui:uiSplash` (add key validation logic)

### Phase 4 — Navigation & wiring
- Update `:navigation` (`AppRoutes`, `AppNavigation`)
- Update `:ui:uiMain` (remove old Main screen, add host for new flow)
- Update `:diApp` (wire all new Koin modules)
- API key setup in each app module

### Phase 5 — Validation & polish
- Verify HERE SDK integration on Android emulator and iOS simulator
- Handle edge cases: no internet, location denied, empty search results
- Screenshot tests for all screens

---

## Open Questions / Risks

1. **HERE SDK local distribution**: Both Android (`.aar`) and iOS (`heresdk.xcframework`) are downloaded files. The `.aar` is needed by four modules — use a shared root-level `sdkLibs/` flat-dir repo rather than duplicating it. The `.xcframework` is added to the Xcode project. Both should be stored via Git LFS or a binary artifact store (not committed as plain files).

2. **`SDKNativeEngine` singleton**: The engine is initialized once per app lifecycle. `coreKeyValidationHere` initializes it; all other HERE SDK objects (`SearchEngine`, `RoutingEngine`, `MapView`) assume it is already initialized. Ordering in DI must ensure validation runs before any other HERE component is created.

3. **Android Context in KMP**: `SDKNativeEngine.makeSharedInstance(context, options)` requires an Android `Context`. Use `expect/actual` in `coreKeyValidationHere` — Android actual receives `Context` via Koin `androidContext()`, iOS actual omits it.

4. **MapView Android lifecycle**: `MapView` is a `SurfaceView` subclass that must receive `onCreate`, `onResume`, `onPause`, `onDestroy`, `onSaveInstanceState` calls. In a Compose `AndroidView`, lifecycle events must be forwarded via `LocalLifecycleOwner`.

5. **Desktop map and routing**: No HERE SDK for JVM Desktop. `:ui:uiMapsViewHere` Desktop source set shows a placeholder. `RoutingEngine` and `SearchEngine` are also unavailable on Desktop — Desktop stubs must return empty/error results. Only the key validation check (`SDKNativeEngine`) is relevant on Desktop if needed at all.

6. **Location on Desktop**: GPS not available; `coreLocationNative` Desktop source set emits `null` from `locationFlow()`.

7. **HERE SDK version pinning**: Module was verified against v4.25.1.0 example code. If a different version of the downloaded SDK is used, class names or method signatures may differ.
