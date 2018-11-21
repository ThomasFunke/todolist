package ro.sorin.todolist.service

import com.ktor.finance.util.dbQuery
import org.jetbrains.exposed.sql.*
import ro.sorin.todolist.model.ChangeType
import ro.sorin.todolist.model.DbUpdater
import ro.sorin.todolist.model.TodoItem
import ro.sorin.todolist.model.TodoItems

class TodoListService : TodoListApi {

    private val listeners = mutableMapOf<Int, suspend (DbUpdater<TodoItem?>) -> Unit>()

    override fun addChangeListener(id: Int, listener: suspend (DbUpdater<TodoItem?>) -> Unit) {
        listeners[id] = listener
    }

    override fun removeChangeListener(id: Int) = listeners.remove(id)

    private suspend fun onChange(type: ChangeType, id: Int, entity: TodoItem? = null) {
        listeners.values.forEach { it.invoke(DbUpdater(type, id, entity)) }
    }

    override suspend fun getAllTodoItems(): List<TodoItem> = dbQuery {
        TodoItems.selectAll().map { toTodoItem(it) }
    }

    private fun toTodoItem(row: ResultRow): TodoItem = TodoItem(
        id = row[TodoItems.id],
        name = row[TodoItems.name],
        quantity = row[TodoItems.quantity],
        dateUpdated = row[TodoItems.dateUpdated]
    )

    override suspend fun getTodoItem(id: Int): TodoItem? = dbQuery {
        TodoItems.select { TodoItems.id.eq(id) }.mapNotNull {
            toTodoItem(it)
        }.singleOrNull()
    }

    override suspend fun addTodoItem(message: TodoItem): TodoItem? {
        var key: Int? = 0
        dbQuery {
            key = TodoItems.insert { it ->
                it[name] = message.name
                it[quantity] = message.quantity
                it[dateUpdated] = message.dateUpdated
            } get TodoItems.id
        }
        return key?.let { getTodoItem(it) }
    }

    override suspend fun updateTodoItem(item: TodoItem): TodoItem? {
        val id = item.id
        return if (id == null) {
            addTodoItem(item)
        } else {
            dbQuery {
                TodoItems.update({ TodoItems.id eq id }) {
                    it[name] = item.name
                    it[quantity] = item.quantity
                    it[dateUpdated] = System.currentTimeMillis()
                }
            }
            getTodoItem(id).also {
                onChange(ChangeType.UPDATE, id, it)
            }
        }
    }

    override suspend fun deleteTodoItem(id: Int): Boolean = dbQuery {
        TodoItems.deleteWhere { TodoItems.id eq id } > 0
    }
}