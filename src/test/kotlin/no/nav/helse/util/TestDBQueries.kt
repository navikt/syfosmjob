package no.nav.helse.util

import java.sql.Timestamp
import no.nav.helse.db.DatabaseInterface
import no.nav.helse.db.toList
import java.sql.ResultSet

fun DatabaseInterface.registerStatus(sykmeldingStatusEvent: SykmeldingStatusEvent) {
    connection.use { connection ->
        connection.prepareStatement(
            """
                INSERT INTO sykmeldingstatus(sykmelding_id, event_timestamp, event) VALUES (?, ?, ?) ON CONFLICT DO NOTHING
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

fun DatabaseInterface.hentSykmeldingsopplysninger(): List<Sykmeldingsopplysninger> {
    connection.use { connection ->
        connection.prepareStatement(
            """
                SELECT *
                FROM SYKMELDINGSOPPLYSNINGER;
                """
        ).use {
            return it.executeQuery().toList { toSykmeldingsopplysninger() }
        }
    }
}


fun ResultSet.toSykmeldingsopplysninger(): Sykmeldingsopplysninger =
    Sykmeldingsopplysninger(
        id = getString("id"),
        pasientFnr = getString("pasient_fnr"),
        pasientAktoerId = getString("pasient_aktoer_id"),
        legeFnr = getString("lege_fnr"),
        legeAktoerId = getString("lege_aktoer_id"),
        mottakId = getString("mottak_id"),
        legekontorOrgNr = getString("legekontor_org_nr"),
        legekontorHerId = getString("legekontor_her_id"),
        legekontorReshId = getString("legekontor_resh_id"),
        epjSystemNavn = getString("epj_system_navn"),
        epjSystemVersjon = getString("epj_system_versjon"),
        mottattTidspunkt = getTimestamp("mottatt_tidspunkt").toLocalDateTime(),
        tssid = getString("tss_id")
    )
