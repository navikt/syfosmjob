package no.nav.helse.util

import com.opentable.db.postgres.embedded.EmbeddedPostgres
import java.sql.Connection
import no.nav.helse.db.DatabaseInterface
import org.flywaydb.core.Flyway

class TestDB : DatabaseInterface {
    private var pg: EmbeddedPostgres? = null
    override val connection: Connection
        get() = pg!!.postgresDatabase.connection.apply { autoCommit = false }

    init {
        pg = EmbeddedPostgres.start()
        Flyway.configure().run {
            locations("migration")
            dataSource(pg?.postgresDatabase).load().migrate()
        }
    }

    fun stop() {
        pg?.close()
    }
}

fun Connection.dropData() {
    use { connection ->
        connection.prepareStatement("DELETE FROM sykmeldingsopplysninger").executeUpdate()
        connection.prepareStatement("DELETE FROM sykmeldingstatus").executeUpdate()
        connection.commit()
    }
}
