package pw.androidthanatos.ant.cache

import android.content.Context
import android.content.SharedPreferences
import java.io.InputStream
import java.nio.charset.Charset
import java.util.TreeSet

/**
 * 网络获取文本数据缓存类
 */


class ResponseCache(context: Context){

    private val sp: SharedPreferences by lazy (LazyThreadSafetyMode.SYNCHRONIZED){
        context.getSharedPreferences("Ant-response-cache",Context.MODE_PRIVATE)
    }

    fun  add(key: String ,text: String)= with(sp.edit()){
        putString(key,text)
    }.apply()

    fun get(key: String)= with(sp){
        getString(key, "")
    }

    fun remove(key: String) = with(sp.edit()){
        remove(key).apply()
    }

    fun clearAll()= with(sp.edit()){
        clear().apply()
    }
}