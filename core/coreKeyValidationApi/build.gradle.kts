plugins {
    id("composeMultiplatformConvention")
}
kotlin {
    android.namespace = "template.core.key.validation.api"
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.coreCommon)
        }
    }
}
