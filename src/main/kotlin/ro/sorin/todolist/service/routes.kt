package ro.sorin.todolist.service

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.http.cio.websocket.Frame
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.websocket.webSocket
import ro.sorin.todolist.model.TodoItem
import ro.sorin.todolist.util.mapper

fun Route.toTodoItem(todoListService: TodoListApi) {
    route("/notes") {

        get("/") { call.respond(todoListService.getAllTodoItems()) }

        get("/{id}") {
            val message = call.parameters["id"]?.toInt()?.let { todoListService.getTodoItem(it) }
            if (message == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(message)
        }

        put("/{id}") {
            val message = call.receive<TodoItem>()
            todoListService.updateTodoItem(message)
        }

        post("/") {
            val message = call.receive<TodoItem>()
            todoListService.addTodoItem(message)?.let { messageResult ->
                call.respond(messageResult)
            }
        }

        delete("/{id}") {
            val removed = todoListService.deleteTodoItem(call.parameters["id"]?.toInt()!!)
            if (removed) call.respond(HttpStatusCode.OK)
            else call.respond(HttpStatusCode.NotFound)
        }

        webSocket("/updates") {
            try {
                todoListService.addChangeListener(this.hashCode()) {
                    outgoing.send(Frame.Text(mapper.writeValueAsString(it)))
                }
                while (true) {
                    incoming.receiveOrNull() ?: break
                }
            } finally {
                todoListService.removeChangeListener(this.hashCode())
            }
        }
    }
}

