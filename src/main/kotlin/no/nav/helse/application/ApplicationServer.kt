package no.nav.helse.application

import io.ktor.server.engine.ApplicationEngine
import java.util.concurrent.TimeUnit
import no.nav.syfo.application.ApplicationState

class ApplicationServer(private val applicationServer: ApplicationEngine, private val applicationState: ApplicationState) {
    init {
        Runtime.getRuntime().addShutdownHook(Thread {
            this.applicationServer.stop(10, 10, TimeUnit.SECONDS)
        })
    }

    fun start() {
        applicationServer.start(false)
        applicationState.alive = true
    }
}
