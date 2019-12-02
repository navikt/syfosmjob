package no.nav.helse

data class Environment(
    val applicationPort: Int = getEnvVar("APPLICATION_PORT", "8080").toInt(),
    val databaseName: String = getEnvVar("DATABASE_NAME", "syfosmregister"),
    val applicationName: String = getEnvVar("APPLICATION_NAME", "syfosmjob"),
    val syfosmregisterDBURL: String = getEnvVar("SYFOSMREGISTER_DB_URL"),
    val mountPathVault: String = getEnvVar("MOUNT_PATH_VAULT")
)

fun getEnvVar(varName: String, defaultValue: String? = null) =
        System.getenv(varName) ?: defaultValue ?: throw RuntimeException("Missing required variable \"$varName\"")
