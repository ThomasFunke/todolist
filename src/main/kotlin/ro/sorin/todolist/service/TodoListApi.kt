package ro.sorin.todolist.service

import ro.sorin.todolist.model.DbUpdater
import ro.sorin.todolist.model.TodoItem

interface TodoListApi {
    suspend fun getTodoItem(id: Int): TodoItem?
    suspend fun getAllTodoItems(): List<TodoItem>
    suspend fun addTodoItem(message: TodoItem): TodoItem?
    suspend fun updateTodoItem(message: TodoItem): TodoItem?
    suspend fun deleteTodoItem(id: Int): Boolean
    fun addChangeListener(id: Int, listener: suspend (DbUpdater<TodoItem?>) -> Unit)
    fun removeChangeListener(id: Int): (suspend (DbUpdater<TodoItem?>) -> Unit)?
}