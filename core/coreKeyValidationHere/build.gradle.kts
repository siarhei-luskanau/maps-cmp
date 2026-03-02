plugins {
    id("composeMultiplatformConvention")
}
kotlin {
    androidLibrary.namespace = "template.core.key.validation.here"
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.coreCommon)
            implementation(projects.core.coreHereSdk)
            implementation(projects.core.coreKeyValidationApi)
        }
        androidMain.dependencies {
            implementation(
                files("${rootProject.projectDir}/libs/heresdk-explore-android/heresdk-explore-android.aar"),
            )
        }
    }
}
