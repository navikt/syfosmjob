package no.nav.helse

import java.time.LocalDateTime
import no.nav.helse.model.StatusEvent
import no.nav.helse.model.SykmeldingStatusEvent
import no.nav.helse.utgattsykmelding.registerSykmeldingerSomSkalSettesTilStatusUtgatt
import no.nav.helse.util.Sykmeldingsopplysninger
import no.nav.helse.util.TestDB
import no.nav.helse.util.dropData
import no.nav.helse.util.hentSykmeldingStatuser
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

        val antallSykmeldingerSomSkalDeaktiveres = database.registerSykmeldingerSomSkalSettesTilStatusUtgatt(utgattDato)

        antallSykmeldingerSomSkalDeaktiveres shouldEqual 0
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

        val antallSykmeldingerSomSkalDeaktiveres = database.registerSykmeldingerSomSkalSettesTilStatusUtgatt(utgattDato)
        antallSykmeldingerSomSkalDeaktiveres shouldEqual 1

        val sykmeldingStatuser = database.hentSykmeldingStatuser()
        sykmeldingStatuser.size shouldEqual 3

        val sykmeldingStatuserUtgatt = sykmeldingStatuser.filter { it.event == StatusEvent.UTGATT }
        sykmeldingStatuserUtgatt.size shouldEqual 1

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

        val antallSykmeldingerSomSkalDeaktiveres = database.registerSykmeldingerSomSkalSettesTilStatusUtgatt(utgattDato)
        antallSykmeldingerSomSkalDeaktiveres shouldEqual 2

        val sykmeldingStatuser = database.hentSykmeldingStatuser()
        sykmeldingStatuser.size shouldEqual 5

        val sykmeldingStatuserUtgatt = sykmeldingStatuser.filter { it.event == StatusEvent.UTGATT }
        sykmeldingStatuserUtgatt.size shouldEqual 2

        database.connection.dropData()
    }

    @Test
    internal fun `Skal oppdatere 1 sykmelding med status utgatt, har mange statuser`() {
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

        val sykmeldingStatusEventNyApenPlus1Hours = SykmeldingStatusEvent(
            sykmeldingId = sykmeldingsopplysningerNy.id,
            timestamp = LocalDateTime.now().plusHours(1),
            event = StatusEvent.APEN
        )
        val sykmeldingStatusEventNyBekreftet = SykmeldingStatusEvent(
            sykmeldingId = sykmeldingsopplysningerNy.id,
            timestamp = LocalDateTime.now().plusHours(2),
            event = StatusEvent.BEKREFTET
        )

        val sykmeldingStatusEventNyApenPlus3Hours = SykmeldingStatusEvent(
            sykmeldingId = sykmeldingsopplysningerNy.id,
            timestamp = LocalDateTime.now().plusHours(3),
            event = StatusEvent.APEN
        )

        database.opprettSykmeldingsopplysninger(sykmeldingsopplysningerNy)
        database.registerStatus(sykmeldingStatusEventNyApenPlus1Hours)
        database.registerStatus(sykmeldingStatusEventNyBekreftet)
        database.registerStatus(sykmeldingStatusEventNyApenPlus3Hours)

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

        val antallSykmeldingerSomSkalDeaktiveres = database.registerSykmeldingerSomSkalSettesTilStatusUtgatt(utgattDato)
        antallSykmeldingerSomSkalDeaktiveres shouldEqual 2

        val sykmeldingStatuser = database.hentSykmeldingStatuser()
        sykmeldingStatuser.size shouldEqual 7

        val sykmeldingStatuserUtgatt = sykmeldingStatuser.filter { it.event == StatusEvent.UTGATT }
        sykmeldingStatuserUtgatt.size shouldEqual 2

        database.connection.dropData()
    }

    @Test
    internal fun `Skal oppdatere 2 sykmelding med status utgatt, har mange ulike statuser`() {
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

        val sykmeldingStatusEventNyApenPlus1Hours = SykmeldingStatusEvent(
            sykmeldingId = sykmeldingsopplysningerNy.id,
            timestamp = sykmeldingsopplysningerNy.mottattTidspunkt.plusHours(1),
            event = StatusEvent.APEN
        )

        database.opprettSykmeldingsopplysninger(sykmeldingsopplysningerNy)
        database.registerStatus(sykmeldingStatusEventNyApenPlus1Hours)

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

        val sykmeldingStatusEvent4MaanderPlusHours2 = SykmeldingStatusEvent(
            sykmeldingId = sykmeldingsopplysninger4Maander.id,
            timestamp = sykmeldingsopplysninger4Maander.mottattTidspunkt.plusHours(2),
            event = StatusEvent.APEN
        )

        database.opprettSykmeldingsopplysninger(sykmeldingsopplysninger4Maander)
        database.registerStatus(sykmeldingStatusEvent4Maander)
        database.registerStatus(sykmeldingStatusEvent4MaanderPlusHours2)

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

        val sykmeldingStatusEvent5MaanderApenPlus1Hours = SykmeldingStatusEvent(
            sykmeldingId = sykmeldingsopplysninger5Maander.id,
            timestamp = sykmeldingsopplysninger5Maander.mottattTidspunkt.plusHours(1),
            event = StatusEvent.APEN
        )

        val sykmeldingStatusEvent5MaanderBekreftetPlus2Hours = SykmeldingStatusEvent(
            sykmeldingId = sykmeldingsopplysninger5Maander.id,
            timestamp = sykmeldingsopplysninger5Maander.mottattTidspunkt.plusHours(2),
            event = StatusEvent.BEKREFTET
        )

        val sykmeldingStatusEvent5MaanderApenPlus3Hours = SykmeldingStatusEvent(
            sykmeldingId = sykmeldingsopplysninger5Maander.id,
            timestamp = sykmeldingsopplysninger5Maander.mottattTidspunkt.plusHours(3),
            event = StatusEvent.APEN
        )

        database.opprettSykmeldingsopplysninger(sykmeldingsopplysninger5Maander)
        database.registerStatus(sykmeldingStatusEvent5MaanderApenPlus1Hours)
        database.registerStatus(sykmeldingStatusEvent5MaanderBekreftetPlus2Hours)
        database.registerStatus(sykmeldingStatusEvent5MaanderApenPlus3Hours)

        val utgattDato = finnUtgaatDato()

        val antallSykmeldingerSomSkalDeaktiveres = database.registerSykmeldingerSomSkalSettesTilStatusUtgatt(utgattDato)
        antallSykmeldingerSomSkalDeaktiveres shouldEqual 2

        val sykmeldingStatuser = database.hentSykmeldingStatuser()
        sykmeldingStatuser.size shouldEqual 8

        val sykmeldingStatuserUtgatt = sykmeldingStatuser.filter { it.event == StatusEvent.UTGATT }
        sykmeldingStatuserUtgatt.size shouldEqual 2

        database.connection.dropData()
    }

    @Test
    internal fun `Skal oppdatere 1 sykmelding med status utgatt, har mange aapen statuser`() {

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

        val sykmeldingStatusEvent5MaanderApenPlus1Hours = SykmeldingStatusEvent(
            sykmeldingId = sykmeldingsopplysninger5Maander.id,
            timestamp = sykmeldingsopplysninger5Maander.mottattTidspunkt.plusHours(1),
            event = StatusEvent.APEN
        )

        val sykmeldingStatusEvent5MaanderBekreftetPlus2Hours = SykmeldingStatusEvent(
            sykmeldingId = sykmeldingsopplysninger5Maander.id,
            timestamp = sykmeldingsopplysninger5Maander.mottattTidspunkt.plusHours(2),
            event = StatusEvent.BEKREFTET
        )

        val sykmeldingStatusEvent5MaanderApen = SykmeldingStatusEvent(
            sykmeldingId = sykmeldingsopplysninger5Maander.id,
            timestamp = LocalDateTime.now(),
            event = StatusEvent.APEN
        )

        database.opprettSykmeldingsopplysninger(sykmeldingsopplysninger5Maander)
        database.registerStatus(sykmeldingStatusEvent5MaanderApenPlus1Hours)
        database.registerStatus(sykmeldingStatusEvent5MaanderBekreftetPlus2Hours)
        database.registerStatus(sykmeldingStatusEvent5MaanderApen)

        val utgattDato = finnUtgaatDato()

        val antallSykmeldingerSomSkalDeaktiveres = database.registerSykmeldingerSomSkalSettesTilStatusUtgatt(utgattDato)
        antallSykmeldingerSomSkalDeaktiveres shouldEqual 1

        val sykmeldingStatuser = database.hentSykmeldingStatuser()
        sykmeldingStatuser.size shouldEqual 4

        val sykmeldingStatuserUtgatt = sykmeldingStatuser.filter { it.event == StatusEvent.UTGATT }
        sykmeldingStatuserUtgatt.size shouldEqual 1

        database.connection.dropData()
    }
}
