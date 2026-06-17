plugins {
    id("composeMultiplatformConvention")
}
kotlin {
    android.namespace = "template.ui.maps.view.api"
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.coreCommon)
            implementation(projects.core.coreLocationApi)
            implementation(projects.core.coreMapRouteApi)
        }
    }
}
