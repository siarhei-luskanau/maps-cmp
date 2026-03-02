plugins {
    id("composeMultiplatformConvention")
}
kotlin {
    androidLibrary.namespace = "template.core.address.search.api"
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.coreCommon)
        }
    }
}
