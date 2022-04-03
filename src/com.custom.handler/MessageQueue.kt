package com.custom.handler

import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

class MessageQueue {
    private val queueLock = ReentrantLock()
    private val queueCondition = queueLock.newCondition()

    private val messageLock = ReentrantReadWriteLock()
    private val messageReadLock = messageLock.readLock()
    private val messageWriteLock = messageLock.writeLock()

    private var currentMessage: Message = Message.HEAD_MESSAGE
        get() = messageReadLock.withLock { field }
        set(value) = messageWriteLock.withLock { field = value }

    fun next(): Message {
        while (true) {
            queueLock.withLock {
                if (currentMessage.next == null) {
                    println("[${Thread.currentThread().name}] Message queue is empty, then it is going to await...")
                    queueCondition.await()
                    println("[${Thread.currentThread().name}] Message queue wakes up.")
                }
            }

            val nextMessage = currentMessage.next!!
            nextMessage.next = null
            currentMessage = nextMessage
            return nextMessage
        }
    }

    fun enqueue(msg: Message) {
        messageWriteLock.withLock {
            msg.next = null
            currentMessage.next = msg
            queueLock.withLock { queueCondition.signalAll() }
        }
    }
}