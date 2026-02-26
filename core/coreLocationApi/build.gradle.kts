plugins {
    id("composeMultiplatformConvention")
}
kotlin {
    androidLibrary.namespace = "template.core.location.api"
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.coreMapRouteApi)
        }
    }
}
