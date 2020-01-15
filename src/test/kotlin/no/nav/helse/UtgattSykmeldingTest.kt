package no.nav.helse

import java.time.LocalDateTime
import no.nav.helse.model.StatusEvent
import no.nav.helse.model.SykmeldingStatusEvent
import no.nav.helse.utgattsykmelding.hentSykmeldingerSomSkalSettesTilStatusUtgatt
import no.nav.helse.utgattsykmelding.registererSykmeldingStatus
import no.nav.helse.util.Sykmeldingsopplysninger
import no.nav.helse.util.TestDB
import no.nav.helse.util.dropData
import no.nav.helse.util.hentSykmeldingerMedStatusUtgatt
import no.nav.helse.util.opprettSykmeldingsopplysninger
import no.nav.helse.util.registerStatus
import org.amshove.kluent.shouldEqual
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

        val sykmeldingerSomSkalDeaktiveres = database.hentSykmeldingerSomSkalSettesTilStatusUtgatt(utgattDato)

        sykmeldingerSomSkalDeaktiveres.size shouldEqual 0
        database.connection.dropData()
    }

    @Test
    internal fun `Skal oppdatere 1 sykmelding med status utgatt`() {
        val sykmeldingsopplysningerNy = Sykmeldingsopplysninger(
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

        val sykmeldingStatusEventNy = SykmeldingStatusEvent(
            sykmeldingId = sykmeldingsopplysningerNy.id,
            timestamp = sykmeldingsopplysningerNy.mottattTidspunkt,
            event = StatusEvent.APEN
        )

        database.opprettSykmeldingsopplysninger(sykmeldingsopplysningerNy)
        database.registerStatus(sykmeldingStatusEventNy)

        val sykmeldingsopplysningerGammel = Sykmeldingsopplysninger(
            id = "uuid1",
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
            mottattTidspunkt = LocalDateTime.now().minusMonths(4),
            tssid = "13455"
        )

        val sykmeldingStatusEventGammel = SykmeldingStatusEvent(
            sykmeldingId = sykmeldingsopplysningerGammel.id,
            timestamp = sykmeldingsopplysningerGammel.mottattTidspunkt,
            event = StatusEvent.APEN
        )
        database.opprettSykmeldingsopplysninger(sykmeldingsopplysningerGammel)
        database.registerStatus(sykmeldingStatusEventGammel)

        val utgattDato = finnUtgaatDato()

        val sykmeldingerSomSkalDeaktiveres = database.hentSykmeldingerSomSkalSettesTilStatusUtgatt(utgattDato)
        sykmeldingerSomSkalDeaktiveres.size shouldEqual 1

        database.registererSykmeldingStatus(sykmeldingerSomSkalDeaktiveres)

        val sykmeldingerMedStatusUtgatt = database.hentSykmeldingerMedStatusUtgatt()
        sykmeldingerMedStatusUtgatt.size shouldEqual 1

        database.connection.dropData()
    }

    @Test
    internal fun `Skal oppdatere 2 sykmelding med status utgatt`() {
        val sykmeldingsopplysningerNy = Sykmeldingsopplysninger(
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

        val sykmeldingStatusEventNy = SykmeldingStatusEvent(
            sykmeldingId = sykmeldingsopplysningerNy.id,
            timestamp = sykmeldingsopplysningerNy.mottattTidspunkt,
            event = StatusEvent.APEN
        )

        database.opprettSykmeldingsopplysninger(sykmeldingsopplysningerNy)
        database.registerStatus(sykmeldingStatusEventNy)

        val sykmeldingsopplysninger4Maander = Sykmeldingsopplysninger(
            id = "uuid1",
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
            mottattTidspunkt = LocalDateTime.now().minusMonths(4),
            tssid = "13455"
        )

        val sykmeldingStatusEvent4Maander = SykmeldingStatusEvent(
            sykmeldingId = sykmeldingsopplysninger4Maander.id,
            timestamp = sykmeldingsopplysninger4Maander.mottattTidspunkt,
            event = StatusEvent.APEN
        )
        database.opprettSykmeldingsopplysninger(sykmeldingsopplysninger4Maander)
        database.registerStatus(sykmeldingStatusEvent4Maander)

        val sykmeldingsopplysninger5Maander = Sykmeldingsopplysninger(
            id = "uuid2",
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
            mottattTidspunkt = LocalDateTime.now().minusMonths(5),
            tssid = "13455"
        )

        val sykmeldingStatusEvent5Maander = SykmeldingStatusEvent(
            sykmeldingId = sykmeldingsopplysninger5Maander.id,
            timestamp = sykmeldingsopplysninger5Maander.mottattTidspunkt,
            event = StatusEvent.APEN
        )
        database.opprettSykmeldingsopplysninger(sykmeldingsopplysninger5Maander)
        database.registerStatus(sykmeldingStatusEvent5Maander)

        val utgattDato = finnUtgaatDato()

        val sykmeldingerSomSkalDeaktiveres = database.hentSykmeldingerSomSkalSettesTilStatusUtgatt(utgattDato)
        sykmeldingerSomSkalDeaktiveres.size shouldEqual 2

        database.registererSykmeldingStatus(sykmeldingerSomSkalDeaktiveres)

        val sykmeldingerMedStatusUtgatt = database.hentSykmeldingerMedStatusUtgatt()
        sykmeldingerMedStatusUtgatt.size shouldEqual 2
        database.connection.dropData()
    }
}
