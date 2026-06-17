plugins {
    id("composeMultiplatformConvention")
}
kotlin {
    android.namespace = "template.core.map.route.api"
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.coreCommon)
        }
    }
}
