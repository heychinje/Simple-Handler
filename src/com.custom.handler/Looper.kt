package com.custom.handler

import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

class Looper private constructor(thread: Thread) {
    internal val mMessageQueue = MessageQueue()
    private val mThread = thread

    private val quitFlagReentrantReadWriteLock = ReentrantReadWriteLock()
    private val quitFlagReadLock = quitFlagReentrantReadWriteLock.readLock()
    private val quitFlagWriteLock = quitFlagReentrantReadWriteLock.writeLock()
    private var quitFlag: Boolean? = null
        get() = quitFlagReadLock.withLock { field }
        set(value) = quitFlagWriteLock.withLock { field = value }

    companion

    object {
        private val looperMapReentrantReadWriteLock = ReentrantReadWriteLock()
        private val looperMapReadLock = looperMapReentrantReadWriteLock.readLock()
        private val looperMapWriteLock = looperMapReentrantReadWriteLock.writeLock()
        private val looperMap: MutableMap<Thread, Looper> = mutableMapOf()

        val looper: Looper?
            get() = looperMapReadLock.withLock { looperMap[Thread.currentThread()] }

        fun prepare(): Looper {
            require(looper == null) { "Looper.prepare() must be called once in each thread." }
            val thread = Thread.currentThread()
            val threadLooper = Looper(thread)
            looperMapWriteLock.withLock { looperMap[thread] = threadLooper }
            return threadLooper
        }

        fun loop() {
            requireNotNull(looper) { "Looper.prepare() must be called before Looper.loop()" }
            looper?.quitFlag = false
            while (looper?.quitFlag == false) {
                val me = looper!!.mMessageQueue
                val msg = me.next()
                kotlin.runCatching {
                    println("[${Thread.currentThread().name}] Dispatching message is started.")
                    msg.target?.dispatchMessage(msg)
                    println("[${Thread.currentThread().name}] Dispatching message is stopped. ")
                }.onFailure {
                    it.printStackTrace()
                }
            }
        }

    }

    fun quit() {
        quitFlag = true
        looperMap.remove(Thread.currentThread())
    }
}