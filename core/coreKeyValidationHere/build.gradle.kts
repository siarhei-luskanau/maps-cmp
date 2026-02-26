plugins {
    id("composeMultiplatformConvention")
}
kotlin {
    androidLibrary.namespace = "template.core.key.validation.here"
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.coreKeyValidationApi)
        }
    }
}
