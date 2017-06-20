package pw.androidthanatos.ant.convert

import pw.androidthanatos.ant.Ant
import pw.androidthanatos.ant.cache.ResponseCache
import pw.androidthanatos.ant.controller.AntMethod
import pw.androidthanatos.ant.controller.HttpListener
import pw.androidthanatos.ant.entity.Chain
import pw.androidthanatos.ant.util.getCacheKey
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSocketFactory

/**
 * HttpUrlConnection 默认网络请求插件
 */
class DefaultConvert: Convert {
    override fun httpConvert(isNetWork: Boolean,cache: Boolean, responseCache: ResponseCache, chain: Chain, listener: HttpListener) {
        when(isNetWork){
            true ->{
                val conn = (URL(chain.url).openConnection()) as HttpURLConnection
                conn.requestMethod = chain.method.name
                when(chain.method){
                    AntMethod.GET ->get(conn,cache,responseCache,chain,listener)
                    AntMethod.POST ->post(conn,cache,responseCache,chain,listener)
                    else ->get(conn,cache, responseCache, chain, listener)
                }
            }
            false ->{
                listener.error(Ant.NETWORK_ERROR)
            }
        }

    }

    override fun httpsConvert(isNetWork: Boolean,socketFactory: SSLSocketFactory, cache: Boolean, responseCache: ResponseCache, chain: Chain, listener: HttpListener) {

        when(isNetWork){
            true ->{
                val conn = (URL(chain.url).openConnection()) as HttpsURLConnection
                conn.sslSocketFactory = socketFactory
                conn.requestMethod = chain.method.name
                when(chain.method){
                    AntMethod.GET ->get(conn,cache,responseCache,chain,listener)
                    AntMethod.POST ->post(conn,cache,responseCache,chain,listener)
                    else ->get(conn,cache, responseCache, chain, listener)
                }
            }

            false ->{
                listener.error(Ant.NETWORK_ERROR)
            }
        }
    }

    fun get(conn: HttpURLConnection,cache: Boolean, responseCache: ResponseCache, chain: Chain, listener: HttpListener) {
        conn.setRequestProperty("Accept-Encoding", "identity")
        //获取的是压缩数据
        //conn.setRequestProperty("Accept-Encoding", "gzip, deflate")
        conn.setRequestProperty("Connection", "keep-alive")
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
        chain.headers.forEach { conn.setRequestProperty(it.key,it.value) }
        val code = conn.responseCode
        if (200 == code){
            listener.success(conn.contentLength.toLong(),"",conn.inputStream,cache,responseCache, getCacheKey(chain.url,chain.method,chain.headers))

        }else{
            listener.error(code)
        }
    }

    fun post(conn: HttpURLConnection,cache: Boolean, responseCache: ResponseCache, chain: Chain, listener: HttpListener) {
        conn.setRequestProperty("Accept-Encoding", "identity")
        conn.setRequestProperty("Accept-Encoding", "identity")
        conn.setRequestProperty("Accept-Encoding", "gzip, deflate")
        conn.setRequestProperty("Connection", "keep-alive")
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
        chain.headers.forEach { conn.setRequestProperty(it.key,it.value) }
        if (chain.body.isNotEmpty()){
            conn.doOutput = true
            val dos = DataOutputStream(conn.outputStream)
            dos.write(chain.body.toByteArray())
            dos.flush()
        }
        val code = conn.responseCode
        if (200 == code){
            listener.success(conn.contentLength.toLong(),"",conn.inputStream,cache,responseCache, getCacheKey(chain.url,chain.method,chain.headers))
        }else{
            listener.error(code)
        }
    }



}