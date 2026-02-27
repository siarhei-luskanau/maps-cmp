rootProject.name = "maps-cmp"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
    ":app:androidApp",
    ":app:desktopApp",
    ":core:coreAddressSearchApi",
    ":core:coreAddressSearchHere",
    ":core:coreCommon",
    ":core:coreHereSdk",
    ":core:coreKeyValidationApi",
    ":core:coreKeyValidationHere",
    ":core:coreKeyValidationStub",
    ":core:coreLocationApi",
    ":core:coreLocationPlatform",
    ":core:coreMapRouteApi",
    ":core:coreMapRouteHere",
    ":core:corePref",
    ":diApp",
    ":navigation",
    ":ui:uiCommon",
    ":ui:uiError",
    ":ui:uiMapRoute",
    ":ui:uiMapsViewApi",
    ":ui:uiMapsViewHere",
    ":ui:uiSearch",
    ":ui:uiSplash",
)

pluginManagement {
    includeBuild("convention-plugin-multiplatform")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("android.*")
            }
        }
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("android.*")
            }
        }
        mavenCentral()
    }
}
