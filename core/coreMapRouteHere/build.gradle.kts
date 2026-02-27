plugins {
    id("composeMultiplatformConvention")
}
kotlin {
    androidLibrary.namespace = "template.core.map.route.here"
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.coreHereSdk)
            implementation(projects.core.coreMapRouteApi)
        }
    }
}
