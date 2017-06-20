package pw.androidthanatos.ant.convert

import okhttp3.*
import pw.androidthanatos.ant.Ant
import pw.androidthanatos.ant.cache.ResponseCache
import pw.androidthanatos.ant.controller.AntMethod
import pw.androidthanatos.ant.controller.HttpListener
import pw.androidthanatos.ant.entity.Chain
import pw.androidthanatos.ant.util.getCacheKey
import java.io.IOException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

/**
 * okhttp网络访问插件
 */
class OKHttpConvert: Convert {

    private lateinit var okHttpClient: OkHttpClient
    override fun httpConvert(isNetWork: Boolean, cache: Boolean, responseCache: ResponseCache, chain: Chain, listener: HttpListener) {
        okHttpClient = OkHttpClient()
        if (isNetWork){

            val requestBuilder: Request.Builder = Request.Builder().url(chain.url)
            requestBuilder.addHeader("Accept-Encoding", "identity")
            //requestBuilder.addHeader("Accept-Encoding", "gzip, deflate")
            requestBuilder.addHeader("Connection", "keep-alive")
            requestBuilder.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            requestBuilder.addHeader("Accept","*/*")
            chain.headers.forEach { requestBuilder.addHeader(it.key,it.value) }
            when(chain.method){
                AntMethod.GET -> get(requestBuilder,cache,responseCache,chain,listener)
                AntMethod.POST -> post(requestBuilder,cache,responseCache,chain,listener)
                else -> get(requestBuilder,cache,responseCache,chain,listener)
            }

        }else{
            listener.error(Ant.NETWORK_ERROR)
        }
    }



    override fun httpsConvert(isNetWork: Boolean, socketFactory: SSLSocketFactory, cache: Boolean, responseCache: ResponseCache, chain: Chain, listener: HttpListener) {
        okHttpClient = OkHttpClient().newBuilder().sslSocketFactory(socketFactory,object : X509TrustManager{
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray() }).build()
        if (isNetWork){
            val requestBuilder: Request.Builder = Request.Builder().url(chain.url)
            requestBuilder.addHeader("Accept-Encoding", "identity")
            //requestBuilder.addHeader("Accept-Encoding", "gzip, deflate")
            requestBuilder.addHeader("Connection", "keep-alive")
            requestBuilder.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            requestBuilder.addHeader("Accept","*/*")

            chain.headers.forEach { requestBuilder.addHeader(it.key,it.value) }
            when(chain.method){
                AntMethod.GET -> get(requestBuilder,cache,responseCache,chain,listener)
                AntMethod.POST -> post(requestBuilder,cache,responseCache,chain,listener)
                else -> get(requestBuilder,cache,responseCache,chain,listener)
            }

        }else{
            listener.error(Ant.NETWORK_ERROR)
        }
    }

    private fun get(requestBuilder: Request.Builder, cache: Boolean, responseCache: ResponseCache, chain: Chain, listener: HttpListener) {
        val request: Request = requestBuilder.get().build()
        okHttpClient.newCall(request).enqueue(object :Callback{
            override fun onFailure(call: Call?, e: IOException?) {
                Ant.antLog(e!!.message.toString())
                listener.error(call!!.execute().code())
            }

            override fun onResponse(call: Call?, response: Response?) {
                if (response!!.isSuccessful){
                    listener.success(response.body()!!.contentLength(),"",response.body()!!.byteStream(),cache,responseCache, getCacheKey(chain.url,chain.method,chain.headers))
                }else{
                    listener.error(response.code())
                    Ant.antLog(response.message())
                }
            }

        })
    }

    private fun post(requestBuilder: Request.Builder, cache: Boolean, responseCache: ResponseCache, chain: Chain, listener: HttpListener) {
        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),chain.body)
        //requestBody.
        val request: Request = requestBuilder.post(requestBody).build()
        okHttpClient.newCall(request).enqueue(object :Callback{
            override fun onFailure(call: Call?, e: IOException?) {
                listener.error(call!!.execute().code())
                Ant.antLog(e!!.message.toString())
            }

            override fun onResponse(call: Call?, response: Response?) {
                if (response!!.isSuccessful){
                    listener.success(response.body()!!.contentLength(),"",response.body()!!.byteStream(),cache,responseCache, getCacheKey(chain.url,chain.method,chain.headers))
                }else{
                    listener.error(response.code())
                    Ant.antLog(response.message())
                }
            }

        })
    }
}