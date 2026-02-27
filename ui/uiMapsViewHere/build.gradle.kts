plugins {
    id("composeMultiplatformConvention")
}
kotlin {
    androidLibrary.namespace = "template.ui.maps.view.here"
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.coreHereSdk)
            implementation(projects.core.coreMapRouteApi)
            implementation(projects.ui.uiMapsViewApi)
        }
        androidMain.dependencies {
            implementation(
                files("${rootProject.projectDir}/libs/heresdk-explore-android/heresdk-explore-android.aar"),
            )
        }
    }
}
