package pw.androidthanatos.ant.data

import android.support.annotation.NonNull
import android.util.Log
import org.json.JSONObject
import pw.androidthanatos.ant.cache.ResponseCache
import pw.androidthanatos.ant.controller.HttpListener
import pw.androidthanatos.ant.controller.ResponseListener
import pw.androidthanatos.ant.handler.AntHandler
import pw.androidthanatos.ant.util.parseResponse
import java.io.ByteArrayOutputStream
import java.io.InputStream

/**
 * 字符串类型网络接受数据处理类
 */
class StringListener(@NonNull var listener: ResponseListener<String>): HttpListener {
    override fun success(dataLength: Long ,cacheText: String,stream: InputStream,cache: Boolean, responseCache: ResponseCache,tag: String) {
        parseResponse(String.javaClass as Class<Any>,dataLength,stream,listener,cache,responseCache,tag)
    }

    override fun getCache(text: String) {

        AntHandler.antHandler.post { listener.complate(text) }
    }

    override fun error(errorCode: Int) {
        AntHandler.antHandler.post { listener.failer(errorCode) }
    }

}
/**
 * json类型网络接受数据处理类
 */
class JSONListener(@NonNull var listener: ResponseListener<JSONObject>): HttpListener {
    override fun success(dataLength: Long ,cacheText: String,stream: InputStream,cache: Boolean, responseCache: ResponseCache,tag: String) {
        parseResponse(JSONObject::class.java as Class<Any>,dataLength,stream,listener,cache,responseCache,tag)
    }

    override fun getCache(text: String) {
        AntHandler.antHandler.post { listener.complate(JSONObject(text)) }
    }

    override fun error(errorCode: Int) {
        AntHandler.antHandler.post { listener.failer(errorCode) }
    }

}