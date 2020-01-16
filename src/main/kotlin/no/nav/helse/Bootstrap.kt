package no.nav.helse

import java.time.LocalDateTime
import no.nav.helse.db.Database
import no.nav.helse.db.VaultCredentialService
import no.nav.helse.utgattsykmelding.registerSykmeldingerSomSkalSettesTilStatusUtgatt
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val log: Logger = LoggerFactory.getLogger("no.nav.helse.syfosmjob")

fun main() {
    val environment = Environment()
    val vaultCredentialService = VaultCredentialService()
    val database = Database(environment, vaultCredentialService)
    val sykmeldingerSomSkalSettesTilStatusUtgatt = database.registerSykmeldingerSomSkalSettesTilStatusUtgatt(finnUtgaatDato())
    if (sykmeldingerSomSkalSettesTilStatusUtgatt > 0) {
        log.info("Antall av sykmeldinger som er blitt oppdatert til status UTGATT: {}", sykmeldingerSomSkalSettesTilStatusUtgatt)
    } else {
        log.info("Ingen sykmeldinger er blitt oppdatert med status til UTGATT")
    }

}

fun finnUtgaatDato(): LocalDateTime =
    LocalDateTime.now().minusMonths(3)
