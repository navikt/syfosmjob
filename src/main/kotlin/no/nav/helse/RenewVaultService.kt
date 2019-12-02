package no.nav.helse

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import no.nav.helse.db.VaultCredentialService
import no.nav.helse.vault.Vault
import no.nav.syfo.application.ApplicationState

class RenewVaultService(private val vaultCredentialService: VaultCredentialService, private val applicationState: ApplicationState) {
    fun startRenewTasks() {
        GlobalScope.launch {
            try {
                Vault.renewVaultTokenTask(applicationState)
            } catch (e: Exception) {
                log.error("Noe gikk galt ved fornying av vault-token", e.message)
            } finally {
                applicationState.ready = false
            }
        }

        GlobalScope.launch {
            try {
                vaultCredentialService.runRenewCredentialsTask(applicationState)
            } catch (e: Exception) {
                log.error("Noe gikk galt ved fornying av vault-credentials", e.message)
            } finally {
                applicationState.ready = false
            }
        }
    }
}
