plugins {
    id("composeMultiplatformConvention")
}
kotlin {
    android.namespace = "template.core.address.search.api"
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.coreCommon)
        }
    }
}
