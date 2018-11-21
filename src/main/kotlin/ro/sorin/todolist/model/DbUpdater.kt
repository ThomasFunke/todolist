package ro.sorin.todolist.model

enum class ChangeType { CREATE, UPDATE, DELETE }

data class DbUpdater<T>(val type: ChangeType, val id: Int, val entity: T)