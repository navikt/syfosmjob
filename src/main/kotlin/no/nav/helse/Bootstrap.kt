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
    /*if (sykmeldingerSomSkalSettesTilStatusUtgatt.isNotEmpty()) {
        log.info("Antall av sykmeldinger som skal få oppdatert status til UTGATT: {}", sykmeldingerSomSkalSettesTilStatusUtgatt.size)
        for (status in sykmeldingerSomSkalSettesTilStatusUtgatt) {
            log.info("Sykmeldingen med id {}, kommer til å få status til UTGATT", status.sykmeldingId)
        }
        database.registererSykmeldingStatus(sykmeldingerSomSkalSettesTilStatusUtgatt)
    } else {
        log.info("Antall av sykmeldinger som skal få status til UTGATT: 0")
    }
     */
}

fun finnUtgaatDato(): LocalDateTime =
    LocalDateTime.now().minusMonths(3)
