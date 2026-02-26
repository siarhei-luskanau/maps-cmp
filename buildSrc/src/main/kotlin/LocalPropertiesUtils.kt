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
