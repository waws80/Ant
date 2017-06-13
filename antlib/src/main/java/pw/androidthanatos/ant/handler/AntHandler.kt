package pw.androidthanatos.ant.handler

import android.os.Handler
import android.os.Looper


/**
 * 向主线程发送消息的handler
 */
object AntHandler {

     val antHandler: Handler by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        Handler(Looper.getMainLooper())
    }
}