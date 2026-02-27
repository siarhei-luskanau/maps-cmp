import java.util.Properties

fun hereAccessKeyID(properties: () -> Properties): String =
    (
        System.getProperty("hereAccessKeyID")
            ?: properties().getProperty("hereAccessKeyID")
    ).orEmpty()

fun hereAccessKeySecret(properties: () -> Properties): String =
    (
        System.getProperty("hereAccessKeySecret")
            ?: properties().getProperty("hereAccessKeySecret")
    ).orEmpty()

fun isDataStubEnabled(properties: () -> Properties) =
    (
        System.getProperty("IS_DATA_STUB_ENABLED")
            ?: properties().getProperty("IS_DATA_STUB_ENABLED")
    ).toBoolean()
