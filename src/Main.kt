import com.custom.handler.Handler
import com.custom.handler.Message

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