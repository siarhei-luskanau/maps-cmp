plugins {
    id("composeMultiplatformConvention")
    alias(libs.plugins.roborazzi)
}

kotlin {
    androidLibrary.namespace = "template.ui.error"
    sourceSets {
        commonMain.dependencies {
            implementation(projects.ui.uiCommon)
        }

        androidHostTest.dependencies {
            implementation(libs.robolectric)
            implementation(libs.roborazzi)
            implementation(libs.roborazzi.compose)
        }

        jvmTest.dependencies {
            implementation(libs.roborazzi.compose.desktop)
        }

        iosTest.dependencies {
            implementation(libs.roborazzi.compose.ios)
        }
    }
}

roborazzi.outputDir.set(file("src/screenshots"))
