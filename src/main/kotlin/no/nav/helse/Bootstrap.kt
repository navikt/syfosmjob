package no.nav.helse

import no.nav.helse.db.Database
import no.nav.helse.db.VaultCredentialService
import no.nav.helse.utgatsykmelding.harSykmeldingssatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val log: Logger = LoggerFactory.getLogger("no.nav.helse.syfosmjob")

fun main() {
    val environment = Environment()
    val vaultCredentialService = VaultCredentialService()
    val database = Database(environment, vaultCredentialService)
    val databasekobling = database.harSykmeldingssatus()

    //database.oppdaterStatusTilutgat(LocalDate.now())
}
