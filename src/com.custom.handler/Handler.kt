package com.custom.handler

open class Handler {
    private var interceptor: Interceptor? = null
    private lateinit var mLooper: Looper

    constructor() {
        val looper = requireNotNull(Looper.looper) { "No looper found in current thread." }
        Handler(looper)
    }

    constructor(looper: Looper) {
        mLooper = looper
    }

    interface Interceptor {
        fun handleMessage(message: Message?): Boolean
    }

    open fun dispatchMessage(message: Message?) {
        println("[${Thread.currentThread().name}] onDispatchMessage: message=$message")
        message ?: return
        if (message.block == null) {
            if (interceptor?.handleMessage(message) != true){
                handleMessage(message)
            }
            return
        }
        message.block.invoke()
    }

    open fun handleMessage(message: Message?) {
        println("[${Thread.currentThread().name}] onReceiveMessage: message=$message")
    }

    fun sendMessage(message: Message?) {
        println("[${Thread.currentThread().name}] onSendMessage: message=$message")
        message?.target = this
        mLooper.mMessageQueue.enqueue(message ?: return)
    }

    fun quit(){
        mLooper.quit()
    }
}