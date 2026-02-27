import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("composeMultiplatformConvention")
    alias(libs.plugins.buildConfig)
}

kotlin {
    androidLibrary.namespace = "template.core.heresdk"

    iosArm64 {
        compilations.getByName("main").cinterops.create("heresdk") {
            defFile(project.file("src/nativeInterop/cinterop/heresdk.def"))
            packageName("heresdk")
            compilerOpts(
                "-F${rootProject.projectDir}/libs/heresdk-explore-ios/heresdk.xcframework/ios-arm64",
                "-fmodules",
            )
        }
        binaries.all {
            linkerOpts(
                "-F${rootProject.projectDir}/libs/heresdk-explore-ios/heresdk.xcframework/ios-arm64",
                "-framework",
                "heresdk",
            )
        }
    }

    iosSimulatorArm64 {
        compilations.getByName("main").cinterops.create("heresdk") {
            defFile(project.file("src/nativeInterop/cinterop/heresdk.def"))
            packageName("heresdk")
            compilerOpts(
                "-F${rootProject.projectDir}/libs/heresdk-explore-ios/heresdk.xcframework/ios-arm64_x86_64-simulator",
                "-fmodules",
            )
        }
        binaries.all {
            linkerOpts(
                "-F${rootProject.projectDir}/libs/heresdk-explore-ios/heresdk.xcframework/ios-arm64_x86_64-simulator",
                "-framework",
                "heresdk",
            )
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(
                files("${rootProject.projectDir}/libs/heresdk-explore-android/heresdk-explore-android.aar"),
            )
        }
        jvmMain.dependencies {
            implementation(
                files("${rootProject.projectDir}/libs/heresdk-explore-android/heresdk-explore-mock.jar"),
            )
        }
    }
}

buildConfig {
    packageName(kotlin.androidLibrary.namespace.orEmpty())
    val hereAccessKeyID = hereAccessKeyID { gradleLocalProperties(rootDir, providers) }
    val hereAccessKeySecret = hereAccessKeySecret { gradleLocalProperties(rootDir, providers) }
    buildConfigField("String", "HERE_ACCESS_KEY_ID", "\"$hereAccessKeyID\"")
    buildConfigField("String", "HERE_ACCESS_KEY_SECRET", "\"$hereAccessKeySecret\"")
}
