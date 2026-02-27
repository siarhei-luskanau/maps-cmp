plugins {
    id("composeMultiplatformConvention")
}
kotlin {
    androidLibrary.namespace = "template.core.address.search.here"
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.coreHereSdk)
            implementation(projects.core.coreAddressSearchApi)
        }
        androidMain.dependencies {
            implementation(
                files("${rootProject.projectDir}/libs/heresdk-explore-android/heresdk-explore-android.aar"),
            )
        }
    }
}
