plugins {
    id("composeMultiplatformConvention")
}
kotlin {
    androidLibrary.namespace = "template.core.map.route.here"
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.coreCommon)
            implementation(projects.core.coreHereSdk)
            implementation(projects.core.coreMapRouteApi)
        }
        androidMain.dependencies {
            implementation(
                files("${rootProject.projectDir}/libs/heresdk-explore-android/heresdk-explore-android.aar"),
            )
        }
    }
}
