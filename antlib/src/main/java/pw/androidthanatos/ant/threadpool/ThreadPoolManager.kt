package pw.androidthanatos.ant.threadpool

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import pw.androidthanatos.ant.Ant
import java.util.*
import java.util.concurrent.*

/**
 * 线程池
 */
object ThreadPoolManager {

    private val service = LinkedList<Runnable>()

    private val poolSize = Runtime.getRuntime().availableProcessors()*2+1

    private lateinit var threadPoolExecutor: ExecutorService

    private val exhandler = object :Handler(Looper.myLooper()){
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            Ant.antLog("${service.size}")
            if (!service.isEmpty()){
                threadPoolExecutor.execute(service.removeLast())
            }
        }
    }

    @JvmStatic fun creat(): ThreadPoolManager{
        threadPoolExecutor = Executors.newFixedThreadPool(poolSize)
        return this
    }



    fun  execute(futureTask: Runnable) {
        service.addLast(futureTask)
        exhandler.sendEmptyMessage(0x100)
    }
}