package no.nav.helse.utgatsykmelding

import no.nav.helse.db.DatabaseInterface
import no.nav.helse.db.toList
import java.sql.Connection
import java.time.LocalDate


fun DatabaseInterface.oppdaterStatusTilutgat(ugaatDato: LocalDate) {
    connection.use { connection ->
        connection.prepareStatement(
            """
                UPDATE SYKMELDINGSATUS
                SET event = ?
                WHERE sykmelding_id = ?
            """
        ).use {
                it.setString(1, "UTGATT")
                it.setString(2, "123455")
            it.executeUpdate()
        }
        connection.commit()
    }
}

fun DatabaseInterface.harSykmeldingssatus() =
    connection.use { connection ->
        connection.prepareStatement(
            """
                SELECT *
                FROM SYKMELDINGSATUS;
                """
        ).use {
            it.executeQuery().next()
        }
    }
