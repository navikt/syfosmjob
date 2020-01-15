package no.nav.helse.utgattsykmelding

import java.sql.ResultSet
import java.sql.Timestamp
import java.time.LocalDateTime
import no.nav.helse.db.DatabaseInterface
import no.nav.helse.db.toList
import no.nav.helse.model.StatusEvent
import no.nav.helse.model.SykmeldingStatusEvent

fun DatabaseInterface.hentSykmeldingerSomSkalSettesTilStatusUtgatt(ugattDato: LocalDateTime): List<SykmeldingStatusEvent> =
    connection.use { connection ->
        connection.prepareStatement(
            """
                SELECT sykmelding_id
                FROM sykmeldingstatus
                where event = 'APEN'
                AND sykmeldingstatus.sykmelding_id IN (
                    SELECT id
                    FROM sykmeldingsopplysninger
                    WHERE mottatt_tidspunkt <= ?)
            """
        ).use {
            it.setTimestamp(1, Timestamp.valueOf(ugattDato))
            it.executeQuery().toList { tilSykmeldingStatusEvent() }
        }
    }

fun DatabaseInterface.registererSykmeldingStatus(sykmeldingStatusEvents: List<SykmeldingStatusEvent>) {
    this.connection.use { connection ->
        connection.prepareStatement(
            """                
                INSERT INTO sykmeldingstatus
                (sykmelding_id, event_timestamp, event)
                VALUES (?, ?, ?)
                on conflict do nothing
            """
        ).use {
            for (status in sykmeldingStatusEvents) {
                it.setString(1, status.sykmeldingId)
                it.setTimestamp(2, Timestamp.valueOf(status.timestamp))
                it.setString(3, status.event.name)
                it.addBatch()
            }
            it.executeBatch()
        }

        connection.commit()
    }
}

fun ResultSet.tilSykmeldingStatusEvent(): SykmeldingStatusEvent =
    SykmeldingStatusEvent(
        sykmeldingId = getString("sykmelding_id"),
        timestamp = LocalDateTime.now(),
        event = StatusEvent.UTGATT
    )
