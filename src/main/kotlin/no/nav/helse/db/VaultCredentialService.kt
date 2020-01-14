package no.nav.helse.db

import com.bettercloud.vault.VaultException
import no.nav.helse.vault.Vault
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("no.nav.helse.db")

class VaultCredentialService {
    var leaseDuration: Long = 0

    fun getNewCredentials(mountPath: String, databaseName: String, role: Role): VaultCredentials {
        val path = "$mountPath/creds/$databaseName-$role"
        log.debug("Getting database credentials for path '$path'")
        try {
            val response = Vault.client.logical().read(path)
            val username = checkNotNull(response.data["username"]) { "Username is not set in response from Vault" }
            val password = checkNotNull(response.data["password"]) { "Password is not set in response from Vault" }
            log.debug("Got new credentials (username=$username, leaseDuration=${response.leaseDuration})")
            leaseDuration = response.leaseDuration
            return VaultCredentials(response.leaseId, username, password)
        } catch (e: VaultException) {
            when (e.httpStatusCode) {
                403 -> log.error("Vault denied permission to fetch database credentials for path '$path'", e)
                else -> log.error("Could not fetch database credentials for path '$path'", e)
            }
            throw e
        }
    }
}

data class VaultCredentials(
    val leaseId: String,
    val username: String,
    val password: String
)
