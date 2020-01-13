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

    // Teser at db fungere som forventet
    val databasekobling = database.harSykmeldingssatus()
    log.info("Sjekker database tilkobling status er: $databasekobling")

    //database.oppdaterStatusTilutgat(LocalDate.now())
}
