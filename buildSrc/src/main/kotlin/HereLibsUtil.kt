import org.gradle.api.Project

fun checkAndExtractHereLibs(rootProject: Project) {
    val sdkVersion = "4.25.3.0.264438"

    val androidSdkDir = rootProject.file("libs/heresdk-explore-android")
    val androidSdkZip = rootProject.file("libs/heresdk-explore-android-$sdkVersion.zip")
    if (!androidSdkDir.exists() && androidSdkZip.exists()) {
        println("Extracting $androidSdkZip to $androidSdkDir")
        rootProject.copy {
            from(rootProject.zipTree(androidSdkZip))
            into(androidSdkDir)
        }

        val androidAarSrc =
            rootProject.file(
                "libs/heresdk-explore-android/heresdk-explore-android-$sdkVersion/heresdk-explore-android-$sdkVersion.aar",
            )
        if (androidAarSrc.exists()) {
            println("Copying $androidAarSrc to $androidSdkDir/heresdk-explore-android.aar")
            rootProject.copy {
                from(androidAarSrc)
                into(androidSdkDir)
                rename { "heresdk-explore-android.aar" }
            }
        }

        val androidMockJarSrc =
            rootProject.file(
                "libs/heresdk-explore-android/heresdk-explore-android-$sdkVersion/heresdk-explore-mock-$sdkVersion.jar",
            )
        if (androidMockJarSrc.exists()) {
            println("Copying $androidMockJarSrc to $androidSdkDir/heresdk-explore-mock.jar")
            rootProject.copy {
                from(androidMockJarSrc)
                into(androidSdkDir)
                rename { "heresdk-explore-mock.jar" }
            }
        }
    }

    val iosSdkDir = rootProject.file("libs/heresdk-explore-ios")
    val iosSdkZip = rootProject.file("libs/heresdk-explore-ios-$sdkVersion.zip")
    if (!iosSdkDir.exists() && iosSdkZip.exists()) {
        println("Extracting $iosSdkZip to $iosSdkDir")
        rootProject.copy {
            from(rootProject.zipTree(iosSdkZip))
            into(iosSdkDir)
        }

        val iosTarGz =
            rootProject.file(
                "libs/heresdk-explore-ios/heresdk-explore-ios-$sdkVersion/" +
                    "heresdk-explore-ios-$sdkVersion.tar.gz",
            )
        val iosInternalDir = rootProject.file("libs/heresdk-explore-ios/heresdk-explore-ios-temp")
        if (!iosInternalDir.exists() && iosTarGz.exists()) {
            println("Extracting $iosTarGz to $iosInternalDir")
            rootProject.copy {
                from(rootProject.tarTree(rootProject.resources.gzip(iosTarGz)))
                into(iosInternalDir)
            }
        }

        val iosXcframworkSrc = rootProject.file("libs/heresdk-explore-ios/heresdk-explore-ios-temp/heresdk/frameworks/heresdk.xcframework")
        val iosXcframworkDest = rootProject.file("libs/heresdk-explore-ios/heresdk.xcframework")
        if (iosXcframworkSrc.exists() && !iosXcframworkDest.exists()) {
            println("Copying $iosXcframworkSrc to $iosXcframworkDest")
            rootProject.copy {
                from(iosXcframworkSrc)
                into(iosXcframworkDest)
            }
        }
    }
}
