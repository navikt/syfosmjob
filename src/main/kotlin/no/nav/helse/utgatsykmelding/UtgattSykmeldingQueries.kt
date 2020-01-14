package no.nav.helse.utgatsykmelding

import java.sql.Timestamp
import java.time.LocalDateTime
import no.nav.helse.db.DatabaseInterface

fun DatabaseInterface.oppdaterSykmeldingStatusTilUtgatt(ugattDato: LocalDateTime) {
    connection.use { connection ->
        connection.prepareStatement(
            """
                UPDATE sykmeldingstatus
                SET event = 'UTGATT'
                where event = 'APEN'
                AND sykmeldingstatus.sykmelding_id IN (
                    SELECT id
                    FROM sykmeldingsopplysninger
                    WHERE mottatt_tidspunkt <= ?);
            """
        ).use {
                it.setTimestamp(1, Timestamp.valueOf(ugattDato))
            it.executeUpdate()
        }
        connection.commit()
    }
}
