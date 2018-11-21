package ro.sorin.todolist.model

import org.jetbrains.exposed.sql.Table

object TodoItems : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val name = varchar("name", 255)
    val quantity = integer("quantity")
    val dateUpdated = long("dateUpdated")
}

data class TodoItem(val id: Int?, val name: String, val quantity: Int, val dateUpdated: Long)