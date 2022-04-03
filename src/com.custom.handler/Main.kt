package com.custom.handler

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

fun main(args: Array<String>) {
    testHandler()
}

private fun testHandler(){
    val handlerThread = HandlerThread("Thread-1").apply { start() }
    val handler = object : Handler(handlerThread.looper) {
        override fun handleMessage(message: Message?) {
            super.handleMessage(message)
        }
    }
    for (i in 0..3){
        if (i == 3) {
            handler.quit()
            return
        }
        Thread.sleep(3000)
        println("\n\n")
        handler.sendMessage(Message(100, "message from Main-Thread"))
    }
}

private class HandlerThread(threadName: String) : Thread(threadName) {
    private val lock = ReentrantLock()
    private val condition = lock.newCondition()
    private var mLooper: Looper? = null
    val looper: Looper
        get() {
            if (mLooper == null) {
                lock.withLock { condition.await() }
            }
            return requireNotNull(mLooper)
        }

    override fun run() {
        super.run()
        mLooper = Looper.prepare()
        lock.withLock { condition.signalAll() }
        Looper.loop()
    }
}