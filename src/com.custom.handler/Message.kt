package com.custom.handler

data class Message(
    val what: Int = 0,
    val args: Any? = null,
    val block: (() -> Unit)? = null,
) {
    companion object {
        val HEAD_MESSAGE: Message = Message()
    }

    var target: Handler? = null
    var next: Message? = null
}