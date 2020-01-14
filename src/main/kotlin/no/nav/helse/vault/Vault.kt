package no.nav.helse.vault

import com.bettercloud.vault.SslConfig
import com.bettercloud.vault.Vault
import com.bettercloud.vault.VaultConfig
import java.io.File
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("no.nav.helse.vault")
object Vault {
    private const val MIN_REFRESH_MARGIN = 600_000L // 10 minutes
    private val vaultToken: String = System.getenv("VAULT_TOKEN")
            ?: getTokenFromFile()
            ?: throw RuntimeException("Neither VAULT_TOKEN or VAULT_TOKEN_PATH is set")
    val client: Vault = Vault(
            VaultConfig()
                    .address(System.getenv("VAULT_ADDR") ?: "https://vault.adeo.no")
                    .token(vaultToken)
                    .openTimeout(5)
                    .readTimeout(30)
                    .sslConfig(SslConfig().build())
                    .build()
    )

    private fun getTokenFromFile(): String? =
            File(System.getenv("VAULT_TOKEN_PATH") ?: "/var/run/secrets/nais.io/vault/vault_token").let { file ->
                when (file.exists()) {
                    true -> file.readText(Charsets.UTF_8).trim()
                    false -> null
                }
            }
}
