package no.nav.helse

import java.time.LocalDateTime
import no.nav.helse.db.Database
import no.nav.helse.db.VaultCredentialService
import no.nav.helse.utgattsykmelding.oppdaterSykmeldingStatusTilUtgatt
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val log: Logger = LoggerFactory.getLogger("no.nav.helse.syfosmjob")

fun main() {
    val environment = Environment()
    val vaultCredentialService = VaultCredentialService()
    val database = Database(environment, vaultCredentialService)
    log.info("Kjører database spørring, og setter status til UTGATT på sykmeldinger som er utgått")
    val antallUtgatt = database.oppdaterSykmeldingStatusTilUtgatt(finnUtgaatDato())
    log.info("Antall meldinger som ble satt til UTGATT {}", antallUtgatt)
}

fun finnUtgaatDato(): LocalDateTime =
    LocalDateTime.now().minusMonths(3)
