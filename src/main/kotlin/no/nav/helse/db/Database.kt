package no.nav.helse.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import java.sql.ResultSet
import no.nav.helse.Environment

enum class Role {
    ADMIN, USER, READONLY;

    override fun toString() = name.toLowerCase()
}

class Database(
    private val env: Environment,
    vaultCredentialService: VaultCredentialService
) : DatabaseInterface {
    private val dataSource: HikariDataSource

    override val connection: Connection
        get() = dataSource.connection

    init {
        val initialCredentials = vaultCredentialService.getNewCredentials(
            mountPath = env.mountPathVault,
            databaseName = env.databaseName,
            role = Role.USER
        )
        dataSource = HikariDataSource(HikariConfig().apply {
            jdbcUrl = env.syfosmregisterDBURL
            username = initialCredentials.username
            password = initialCredentials.password
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        })
    }
}

fun <T> ResultSet.toList(mapper: ResultSet.() -> T) = mutableListOf<T>().apply {
    while (next()) {
        add(mapper())
    }
}

interface DatabaseInterface {
    val connection: Connection
}
