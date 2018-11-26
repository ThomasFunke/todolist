package ro.sorin.todolist

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.http.ContentType
import io.ktor.jackson.JacksonConverter
import io.ktor.jackson.jackson
import io.ktor.routing.Routing
import io.ktor.server.netty.EngineMain
import io.ktor.websocket.WebSockets
import ro.sorin.todolist.service.TodoListService
import ro.sorin.todolist.service.toTodoItem
import ro.sorin.todolist.util.initExposedDb
import ro.sorin.todolist.util.mapper
import java.text.DateFormat

fun Application.module() {
    install(CallLogging)
    install(DefaultHeaders)
    install(WebSockets)
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
            disableDefaultTyping()
            dateFormat = DateFormat.getDateInstance()
            register(ContentType.Application.Json, JacksonConverter(mapper))
        }
    }
    install(Routing) {
        toTodoItem(TodoListService())
    }
    initExposedDb()
}

fun main(args: Array<String>): Unit = EngineMain.main(args)
