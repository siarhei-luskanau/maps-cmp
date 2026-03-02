import org.gradle.api.Project

const val SDK_VERSION = "4.25.3.0.264438"

fun splitHereLibs(rootProject: Project) {
    val partSize = 49 * 1024 * 1024L
    listOf("android", "ios").forEach { platform ->
        val zip = rootProject.file("libs/heresdk-explore-$platform-$SDK_VERSION.zip")
        if (!zip.exists()) return@forEach
        val partOne = rootProject.file("libs/heresdk-explore-$platform-$SDK_VERSION.001")
        if (partOne.exists()) return@forEach
        println("Splitting $zip into parts of ${partSize / 1024 / 1024} MB")
        var partCount = 0
        zip.inputStream().buffered().use { input ->
            val buffer = ByteArray(partSize.toInt())
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } > 0) {
                val partFile = rootProject.file("libs/heresdk-explore-$platform-$SDK_VERSION.%03d".format(++partCount))
                partFile.writeBytes(if (bytesRead == buffer.size) buffer else buffer.copyOf(bytesRead))
            }
        }
        println("Split complete: $partCount parts created for $platform SDK")
    }
}

fun joinHereLibParts(rootProject: Project) {
    listOf("android", "ios").forEach { platform ->
        val partOne = rootProject.file("libs/heresdk-explore-$platform-$SDK_VERSION.001")
        if (!partOne.exists()) return@forEach
        val zip = rootProject.file("libs/heresdk-explore-$platform-$SDK_VERSION.zip")
        if (zip.exists()) return@forEach
        println("Joining parts into $zip")
        var partIndex = 1
        zip.outputStream().buffered().use { output ->
            while (true) {
                val partFile = rootProject.file("libs/heresdk-explore-$platform-$SDK_VERSION.%03d".format(partIndex++))
                if (!partFile.exists()) break
                partFile.inputStream().use { it.copyTo(output) }
            }
        }
        println("Join complete: ${partIndex - 1} parts merged for $platform SDK")
    }
}

fun checkAndExtractHereLibs(rootProject: Project) {
    val androidSdkDir = rootProject.file("libs/heresdk-explore-android")
    val androidSdkZip = rootProject.file("libs/heresdk-explore-android-$SDK_VERSION.zip")
    if (!androidSdkDir.exists() && androidSdkZip.exists()) {
        println("Extracting $androidSdkZip to $androidSdkDir")
        rootProject.copy {
            from(rootProject.zipTree(androidSdkZip))
            into(androidSdkDir)
        }

        val androidAarSrc =
            rootProject.file(
                "libs/heresdk-explore-android/heresdk-explore-android-$SDK_VERSION/heresdk-explore-android-$SDK_VERSION.aar",
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
                "libs/heresdk-explore-android/heresdk-explore-android-$SDK_VERSION/heresdk-explore-mock-$SDK_VERSION.jar",
            )
        if (androidMockJarSrc.exists()) {
            println("Copying $androidMockJarSrc to $androidSdkDir/heresdk-explore-mock.jar")
            rootProject.copy {
                from(androidMockJarSrc)
                into(androidSdkDir)
                rename { "heresdk-explore-mock.jar" }
            }
        }

        val examplesZipSrc =
            rootProject.file(
                "libs/heresdk-explore-android/heresdk-explore-android-$SDK_VERSION/" +
                    "heresdk-explore-android-examples-${SDK_VERSION.substringBeforeLast(".")}.zip",
            )
        if (examplesZipSrc.exists()) {
            println("Extracting $examplesZipSrc to ${examplesZipSrc.parentFile}")
            rootProject.copy {
                from(rootProject.zipTree(examplesZipSrc))
                into(examplesZipSrc.path.removeSuffix(".zip"))
            }
        }

        val examplesLibsDir =
            rootProject.file(
                "libs/heresdk-explore-android/heresdk-explore-android-$SDK_VERSION/" +
                    "heresdk-explore-android-examples-${SDK_VERSION.substringBeforeLast(".")}/" +
                    "HelloMapKotlinJC/app/libs",
            )
        if (examplesLibsDir.exists()) {
            println("Copying *.aar from $examplesLibsDir to $androidSdkDir")
            rootProject.copy {
                from(examplesLibsDir)
                into(androidSdkDir)
                include("*.aar")
            }
        }
    }

    val iosSdkDir = rootProject.file("libs/heresdk-explore-ios")
    val iosSdkZip = rootProject.file("libs/heresdk-explore-ios-$SDK_VERSION.zip")
    if (!iosSdkDir.exists() && iosSdkZip.exists()) {
        println("Extracting $iosSdkZip to $iosSdkDir")
        rootProject.copy {
            from(rootProject.zipTree(iosSdkZip))
            into(iosSdkDir)
        }

        val iosTarGz =
            rootProject.file(
                "libs/heresdk-explore-ios/heresdk-explore-ios-$SDK_VERSION/" +
                    "heresdk-explore-ios-$SDK_VERSION.tar.gz",
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

        val iosExamplesZipSrc =
            rootProject.file(
                "libs/heresdk-explore-ios/heresdk-explore-ios-$SDK_VERSION/" +
                    "heresdk-explore-ios-examples-${SDK_VERSION.substringBeforeLast(".")}.zip",
            )
        if (iosExamplesZipSrc.exists()) {
            println("Extracting $iosExamplesZipSrc to ${iosExamplesZipSrc.parentFile}")
            rootProject.copy {
                from(rootProject.zipTree(iosExamplesZipSrc))
                into(iosExamplesZipSrc.path.removeSuffix(".zip"))
            }
        }
    }
}
