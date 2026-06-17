plugins {
    id("composeMultiplatformConvention")
}
kotlin {
    android.namespace = "template.core.location.platform"
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.coreCommon)
            implementation(projects.core.coreLocationApi)
        }
    }
}
