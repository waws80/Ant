package pw.androidthanatos.ant.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Base64
import android.util.Log
import org.json.JSONObject
import pw.androidthanatos.ant.cache.ResponseCache
import pw.androidthanatos.ant.controller.AntMethod
import pw.androidthanatos.ant.controller.ResponseListener
import pw.androidthanatos.ant.handler.AntHandler
import pw.androidthanatos.ant.img.ByteArrayListener
import java.io.InputStream
import java.nio.charset.Charset

/**
 * 工具类
 */

/**
 * 解析数据并将数据解析成对应的类型然后返回到主线程
 */
fun <T> parseResponse(responseClass: Class<Any>, dataLength: Long , stream: InputStream , listener: ResponseListener<T>,
                      cache: Boolean,responseCache: ResponseCache,tag: String){

    val bos = PaseIO.parseIo(stream,dataLength,listener)
    val text = String(bos.toByteArray())
    if (cache){
        val arr= mutableListOf<Any>()
        arr.add(dataLength)
        val str = Base64.encodeToString(text.toByteArray(Charset.forName("UTF-8")),Base64.DEFAULT)
        arr.add(str)
        responseCache.add(tag,arr.toString())
    }
    when(responseClass){
        String::class.java ->{
            AntHandler.antHandler.post { listener.complate(text as T) }
        }
        JSONObject::class.java ->{
            AntHandler.antHandler.post { listener.complate(JSONObject(text) as T) }
        }
        ByteArrayListener.MyData::class.java -> {
            listener.complate(bos.toByteArray() as T)
        }
        else ->{
            AntHandler.antHandler.post { listener.complate(text as T) }
        }
    }


}

/**
 * 生成网络请求数据缓存key
 */
fun getCacheKey(url: String, method: AntMethod,headers: HashMap<String,String>): String{
    val sb = StringBuffer()
    sb.append(url)
    sb.append(method.name)
    headers.forEach { sb.append(it.key+it.value) }
    val byteArr = sb.toString().toByteArray(Charset.forName("UTF-8"))
    return String(Base64.encode(byteArr, Base64.DEFAULT), Charset.forName("UTF-8"))
}

/**
 * 判断当前设备是否有网
 * true  有网   false 没网
 */
fun isNetWorkAvailable(context: Context): Boolean{
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val nk = cm.allNetworkInfo
    var a = false
    if (nk != null && nk.isNotEmpty()){
        nk.forEach {
            if (it.state == NetworkInfo.State.CONNECTED){
                a = true
            }
        }
    }
    return a
}