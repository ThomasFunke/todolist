package ro.sorin.todolist.util

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

fun initExposedDb() {
  Log().debug("Connecting to DB...")
  Database.connect(hikari).also {
    transaction {
      addLogger(StdOutSqlLogger)
    }
  }
}

private val hikari by lazy(LazyThreadSafetyMode.NONE) {
  HikariDataSource(HikariConfig().apply {
    driverClassName = "org.h2.Driver"
    jdbcUrl = "jdbc:h2:mem:test"
    maximumPoolSize = 3
    isAutoCommit = false
    transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    validate()
  })
}

suspend fun <T> dbQuery(block: () -> T): T = coroutineScope {
  async(Dispatchers.IO) { transaction { block() } }.await()
}