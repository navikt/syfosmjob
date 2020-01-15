package no.nav.helse

import java.time.LocalDateTime
import no.nav.helse.db.Database
import no.nav.helse.db.VaultCredentialService
import no.nav.helse.utgattsykmelding.hentSykmeldingerSomSkalSettesTilStatusUtgatt
import no.nav.helse.utgattsykmelding.registererSykmeldingStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val log: Logger = LoggerFactory.getLogger("no.nav.helse.syfosmjob")

fun main() {
    val environment = Environment()
    val vaultCredentialService = VaultCredentialService()
    val database = Database(environment, vaultCredentialService)
    val sykmeldingerSomSkalSettesTilStatusUtgatt = database.hentSykmeldingerSomSkalSettesTilStatusUtgatt(finnUtgaatDato())
    if (sykmeldingerSomSkalSettesTilStatusUtgatt.isNotEmpty()) {
        log.info("Antall av sykmeldinger som skal få oppdatert status til UTGATT: {}", sykmeldingerSomSkalSettesTilStatusUtgatt.size)
        database.registererSykmeldingStatus(sykmeldingerSomSkalSettesTilStatusUtgatt)
    } else {
        log.info("Antall av sykmeldinger som skal få oppdatert status til UTGATT: 0")
    }
}

fun finnUtgaatDato(): LocalDateTime =
    LocalDateTime.now().minusMonths(3)
