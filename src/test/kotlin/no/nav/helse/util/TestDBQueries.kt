package no.nav.helse.util

import java.sql.ResultSet
import java.sql.Timestamp
import no.nav.helse.db.DatabaseInterface
import no.nav.helse.db.toList
import no.nav.helse.model.StatusEvent
import no.nav.helse.model.SykmeldingStatusEvent

fun DatabaseInterface.registerStatus(sykmeldingStatusEvent: SykmeldingStatusEvent) {
    connection.use { connection ->
        connection.prepareStatement(
            """
                INSERT INTO sykmeldingstatus(sykmelding_id, event_timestamp, event) VALUES (?, ?, ?)
                """
        ).use {
            it.setString(1, sykmeldingStatusEvent.sykmeldingId)
            it.setTimestamp(2, Timestamp.valueOf(sykmeldingStatusEvent.timestamp))
            it.setString(3, sykmeldingStatusEvent.event.name)
            it.execute()
        }
        connection.commit()
    }
}

fun DatabaseInterface.opprettSykmeldingsopplysninger(sykmeldingsopplysninger: Sykmeldingsopplysninger) {
    connection.use { connection ->
        connection.prepareStatement(
            """
            INSERT INTO SYKMELDINGSOPPLYSNINGER(
                id,
                pasient_fnr,
                pasient_aktoer_id,
                lege_fnr,
                lege_aktoer_id,
                mottak_id,
                legekontor_org_nr,
                legekontor_her_id,
                legekontor_resh_id,
                epj_system_navn,
                epj_system_versjon,
                mottatt_tidspunkt,
                tss_id)
            VALUES  (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """
        ).use {
            it.setString(1, sykmeldingsopplysninger.id)
            it.setString(2, sykmeldingsopplysninger.pasientFnr)
            it.setString(3, sykmeldingsopplysninger.pasientAktoerId)
            it.setString(4, sykmeldingsopplysninger.legeFnr)
            it.setString(5, sykmeldingsopplysninger.legeAktoerId)
            it.setString(6, sykmeldingsopplysninger.mottakId)
            it.setString(7, sykmeldingsopplysninger.legekontorOrgNr)
            it.setString(8, sykmeldingsopplysninger.legekontorHerId)
            it.setString(9, sykmeldingsopplysninger.legekontorReshId)
            it.setString(10, sykmeldingsopplysninger.epjSystemNavn)
            it.setString(11, sykmeldingsopplysninger.epjSystemVersjon)
            it.setTimestamp(12, Timestamp.valueOf(sykmeldingsopplysninger.mottattTidspunkt))
            it.setString(13, sykmeldingsopplysninger.tssid)
            it.executeUpdate()
        }
        connection.commit()
    }
}

fun DatabaseInterface.hentSykmeldingStatuser(): List<SykmeldingStatusEvent> =
    connection.use { connection ->
        connection.prepareStatement(
            """
                SELECT sykmelding_id, event_timestamp, event
                FROM sykmeldingstatus
            """
        ).use {
            it.executeQuery().toList { tilSykmeldingStatusEvent() }
        }
    }

fun ResultSet.tilSykmeldingStatusEvent(): SykmeldingStatusEvent =
    SykmeldingStatusEvent(
        sykmeldingId = getString("sykmelding_id"),
        timestamp = getTimestamp("event_timestamp").toLocalDateTime(),
        event = tilStatusEvent(getString("event"))
    )

private fun tilStatusEvent(status: String): StatusEvent {
    return when (status) {
        "BEKREFTET" -> StatusEvent.BEKREFTET
        "APEN" -> StatusEvent.APEN
        "SENDT" -> StatusEvent.SENDT
        "AVBRUTT" -> StatusEvent.AVBRUTT
        "UTGATT" -> StatusEvent.UTGATT
        else -> throw IllegalStateException("Sykmeldingen har ukjent status eller er slettet, skal ikke kunne skje")
    }
}
