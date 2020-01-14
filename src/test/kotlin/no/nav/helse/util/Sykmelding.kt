package no.nav.helse.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.time.LocalDateTime
import no.nav.syfo.model.Sykmelding
import org.postgresql.util.PGobject

val objectMapper: ObjectMapper = ObjectMapper().apply {
    registerKotlinModule()
    registerModule(JavaTimeModule())
    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
}

data class Sykmeldingsopplysninger(
    val id: String,
    val pasientFnr: String,
    val pasientAktoerId: String,
    val legeFnr: String,
    val legeAktoerId: String,
    val mottakId: String,
    val legekontorOrgNr: String?,
    val legekontorHerId: String?,
    val legekontorReshId: String?,
    val epjSystemNavn: String,
    val epjSystemVersjon: String,
    val mottattTidspunkt: LocalDateTime,
    val tssid: String?
)

data class Sykmeldingsdokument(
    val id: String,
    val sykmelding: Sykmelding
)

fun Sykmelding.toPGObject() = PGobject().also {
    it.type = "json"
    it.value = objectMapper.writeValueAsString(this)
}
