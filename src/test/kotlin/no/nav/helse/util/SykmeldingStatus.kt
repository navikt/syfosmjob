package no.nav.helse.util

import java.time.LocalDateTime

data class SykmeldingStatusEvent(
    val sykmeldingId: String,
    val timestamp: LocalDateTime,
    val event: StatusEvent
)

enum class StatusEvent {
    APEN, AVBRUTT, UTGATT, SENDT, BEKREFTET, SLETTET
}
