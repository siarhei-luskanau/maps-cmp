plugins {
    id("composeMultiplatformConvention")
}
kotlin {
    androidLibrary.namespace = "template.core.address.search.here"
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.coreAddressSearchApi)
        }
    }
}
