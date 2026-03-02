import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("composeMultiplatformConvention")
    alias(libs.plugins.roborazzi)
}

kotlin {
    androidLibrary.namespace = "template.di.app"

    targets
        .withType<KotlinNativeTarget>()
        .matching { it.konanTarget.family.isAppleFamily }
        .configureEach {
            binaries.withType<Framework>().configureEach {
                export(projects.core.coreHereSdk)
            }
        }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.coreAddressSearchApi)
            implementation(projects.core.coreAddressSearchHere)
            implementation(projects.core.coreCommon)
            implementation(projects.core.coreKeyValidationApi)
            implementation(projects.core.coreLocationApi)
            implementation(projects.core.coreLocationPlatform)
            implementation(projects.core.coreMapRouteApi)
            implementation(projects.core.coreMapRouteHere)
            implementation(projects.core.corePref)
            implementation(projects.navigation)
            implementation(projects.ui.uiCommon)
            implementation(projects.ui.uiError)
            implementation(projects.ui.uiMapRoute)
            implementation(projects.ui.uiMapsViewApi)
            implementation(projects.ui.uiMapsViewHere)
            implementation(projects.ui.uiSearch)
            implementation(projects.ui.uiSplash)
            if (isDataStubEnabled { gradleLocalProperties(rootDir, providers) }) {
                implementation(projects.core.coreKeyValidationStub)
            } else {
                implementation(projects.core.coreKeyValidationHere)
            }
        }

        jvmMain.dependencies {
            implementation(libs.androidx.datastore.core.okio)
        }

        jvmTest.dependencies {
            implementation(libs.roborazzi.compose.desktop)
        }

        androidMain.dependencies {
            implementation(libs.androidx.datastore.core.okio)
        }

        androidHostTest.dependencies {
            implementation(libs.robolectric)
            implementation(libs.roborazzi)
            implementation(libs.roborazzi.compose)
        }

        iosMain.dependencies {
            api(projects.core.coreHereSdk)
            implementation(libs.androidx.datastore.core.okio)
        }

        iosTest.dependencies {
            implementation(libs.roborazzi.compose.ios)
        }
    }
}

// Directory for reference images
roborazzi.outputDir.set(file("src/screenshots"))
