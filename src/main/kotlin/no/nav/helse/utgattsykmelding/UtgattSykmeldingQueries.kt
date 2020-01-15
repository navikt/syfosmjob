package no.nav.helse.utgattsykmelding

import java.sql.Timestamp
import java.time.LocalDateTime
import no.nav.helse.db.DatabaseInterface

fun DatabaseInterface.hentSykmeldingerSomSkalSettesTilStatusUtgatt(ugattDato: LocalDateTime): Int {
    connection.use { connection ->
    val antallUtgatt = connection.prepareStatement(
            """
                INSERT INTO sykmeldingstatus
                SELECT ss.sykmelding_id, CURRENT_TIMESTAMP, 'UTGATT'
                FROM sykmeldingstatus AS ss
                where (SELECT event
                              FROM sykmeldingstatus
                              WHERE sykmelding_id = ss.sykmelding_id
                              ORDER BY event_timestamp DESC
                              LIMIT 1) = 'APEN'
                AND ss.event_timestamp <= ?     
            """
        ).use {
            it.setTimestamp(1, Timestamp.valueOf(ugattDato))
        it.executeUpdate()
        }
        connection.commit()

        return antallUtgatt
    }
}
