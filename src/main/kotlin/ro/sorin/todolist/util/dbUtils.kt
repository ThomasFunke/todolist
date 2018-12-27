package ro.sorin.todolist.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import ro.sorin.todolist.model.TodoItems

fun initExposedDb() {
    logger().debug("Connecting to DB...")
    Database.connect(hikari).also {
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(TodoItems)
        }
    }
}

private val hikari by lazy(LazyThreadSafetyMode.NONE) {
    HikariDataSource(HikariConfig().apply {
        driverClassName = "org.h2.Driver"
        jdbcUrl = "jdbc:h2:mem:todolist"
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    })
}

suspend fun <T> dbQuery(block: () -> T): T = coroutineScope {
    withContext(Dispatchers.IO) { transaction { block() } }
}

val mapper = jacksonObjectMapper().apply {
    setSerializationInclusion(JsonInclude.Include.NON_NULL)
}