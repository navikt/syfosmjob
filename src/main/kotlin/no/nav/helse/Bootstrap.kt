package no.nav.helse

import java.time.LocalDateTime
import no.nav.helse.db.Database
import no.nav.helse.db.VaultCredentialService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val log: Logger = LoggerFactory.getLogger("no.nav.helse.syfosmjob")

fun main() {
    val environment = Environment()
    val vaultCredentialService = VaultCredentialService()
    val database = Database(environment, vaultCredentialService)
    log.info("Hello from syfosmjob")
    /*val sykmeldingerSomSkalSettesTilStatusUtgatt = database.registerSykmeldingerSomSkalSettesTilStatusUtgatt(finnUtgaatDato())
    if (sykmeldingerSomSkalSettesTilStatusUtgatt > 0) {
        log.info("Antall sykmeldinger som har fått status UTGATT: {}", sykmeldingerSomSkalSettesTilStatusUtgatt)
    } else {
        log.info("Ingen sykmeldinger har fått status UTGATT")
    }
     */
}

fun finnUtgaatDato(): LocalDateTime =
    LocalDateTime.now().minusMonths(3)
