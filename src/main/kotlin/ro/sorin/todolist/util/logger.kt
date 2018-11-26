package ro.sorin.todolist.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun logger(id: String = "bitchat-server"): Logger = LoggerFactory.getLogger(id)
