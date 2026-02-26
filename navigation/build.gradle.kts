plugins {
    id("composeMultiplatformConvention")
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    androidLibrary.namespace = "template.navigation"
    sourceSets {
        commonMain.dependencies {
            implementation(libs.jetbrains.navigation3.ui)
            implementation(libs.kotlinx.serialization.json)
            implementation(projects.core.coreAddressSearchApi)
            implementation(projects.ui.uiCommon)
            implementation(projects.ui.uiError)
            implementation(projects.ui.uiMapRoute)
            implementation(projects.ui.uiMapsViewApi)
            implementation(projects.ui.uiSearch)
            implementation(projects.ui.uiSplash)
        }
    }
}
