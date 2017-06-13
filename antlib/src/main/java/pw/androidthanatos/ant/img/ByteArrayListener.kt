package pw.androidthanatos.ant.img

import android.support.annotation.NonNull
import pw.androidthanatos.ant.cache.ResponseCache
import pw.androidthanatos.ant.controller.HttpListener
import pw.androidthanatos.ant.controller.ResponseListener
import pw.androidthanatos.ant.util.parseResponse
import java.io.InputStream

/**
 * byteArry类型网络接受数据处理类 主要用来获取图片使用
 */
class ByteArrayListener(@NonNull var listener: ResponseListener<ByteArray>): HttpListener {
    override fun success(dataLength: Long,cacheText: String, stream: InputStream, cache: Boolean, responseCache: ResponseCache, tag: String) {
        val a = MyData::class.java as Class<Any>
        parseResponse(a,dataLength,stream,listener,cache,responseCache,tag)
    }

    override fun getCache(text: String) {
        //不使用
    }
    override fun error(errorCode: Int) {
        listener.failer(errorCode)
    }

    class MyData

}