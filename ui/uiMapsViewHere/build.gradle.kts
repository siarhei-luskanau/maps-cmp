plugins {
    id("composeMultiplatformConvention")
}
kotlin {
    androidLibrary.namespace = "template.ui.maps.view.here"
    sourceSets {
        commonMain.dependencies {
            implementation(projects.ui.uiMapsViewApi)
        }
    }
}
