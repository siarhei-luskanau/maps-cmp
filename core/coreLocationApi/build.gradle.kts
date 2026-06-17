plugins {
    id("composeMultiplatformConvention")
}
kotlin {
    android.namespace = "template.core.location.api"
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.coreCommon)
            implementation(projects.core.coreMapRouteApi)
        }
    }
}
