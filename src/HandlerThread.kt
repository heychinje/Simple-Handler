import com.custom.handler.Looper
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class HandlerThread(name:String):Thread(name) {
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