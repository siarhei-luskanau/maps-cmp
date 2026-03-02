plugins {
    id("composeMultiplatformConvention")
}
kotlin {
    androidLibrary.namespace = "template.ui.maps.view.here"
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.coreCommon)
            implementation(projects.core.coreHereSdk)
            implementation(projects.core.coreMapRouteApi)
            implementation(projects.ui.uiMapsViewApi)
        }
        androidMain.dependencies {
            val hereLibs = "${rootProject.projectDir}/libs/heresdk-explore-android"
            implementation(files("$hereLibs/heresdk-explore-android.aar"))
            implementation(files("$hereLibs/here-sdk-units-core-release-kotlin-v1.0.aar"))
            implementation(files("$hereLibs/here-sdk-units-compass-release-kotlin-v1.0.aar"))
            implementation(files("$hereLibs/here-sdk-units-mapruler-release-kotlin-v1.0.aar"))
            implementation(files("$hereLibs/here-sdk-units-mapswitcher-release-kotlin-v1.1.aar"))
        }
    }
}
