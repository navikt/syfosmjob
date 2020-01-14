package no.nav.helse

import java.time.LocalDateTime
import no.nav.helse.utgatsykmelding.oppdaterSykmeldingStatusTilUtgatt
import no.nav.helse.util.StatusEvent
import no.nav.helse.util.SykmeldingStatusEvent
import no.nav.helse.util.Sykmeldingsopplysninger
import no.nav.helse.util.TestDB
import no.nav.helse.util.opprettSykmeldingsopplysninger
import no.nav.helse.util.registerStatus
import org.junit.jupiter.api.Test

internal class UtgattSykmeldingTest {
    val database = TestDB()

    @Test
    internal fun `Skal ikkje oppdatere noen statuser, pga ingen over 3 maander gammel`() {
        val sykmeldingsopplysninger = Sykmeldingsopplysninger(
            id = "uuid",
            pasientFnr = "pasientFnr",
            pasientAktoerId = "pasientAktorId",
            legeFnr = "legeFnr",
            legeAktoerId = "legeAktorId",
            mottakId = "eid-1",
            legekontorOrgNr = "lege-orgnummer",
            legekontorHerId = "legekontorHerId",
            legekontorReshId = "legekontorReshId",
            epjSystemNavn = "epjSystemNavn",
            epjSystemVersjon = "epjSystemVersjon",
            mottattTidspunkt = LocalDateTime.now(),
            tssid = "13455"
        )

        val sykmeldingStatusEvent = SykmeldingStatusEvent(
            sykmeldingId = sykmeldingsopplysninger.id,
            timestamp = sykmeldingsopplysninger.mottattTidspunkt,
            event = StatusEvent.APEN
        )

        database.opprettSykmeldingsopplysninger(sykmeldingsopplysninger)
        database.registerStatus(sykmeldingStatusEvent)

        val utgattDato = finnUtgaatDato()

        database.oppdaterSykmeldingStatusTilUtgatt(utgattDato)
    }
}
