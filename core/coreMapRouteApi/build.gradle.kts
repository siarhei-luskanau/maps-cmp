plugins {
    id("composeMultiplatformConvention")
}
kotlin {
    androidLibrary.namespace = "template.core.map.route.api"
    sourceSets {
        commonMain.dependencies {}
    }
}
