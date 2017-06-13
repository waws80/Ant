package pw.androidthanatos.ant.img

import java.security.MessageDigest
import kotlin.experimental.and

/**
 * Created on 2017/1/9.
 * 作者：by thanatos
 * 作用：md5 工具类
 */

object MD5Utils {

    private val  md5: MessageDigest by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        MessageDigest.getInstance("MD5")
    }

    /**
     * 用于获取一个String的md5值
     * @param str
     * @return
     */
    fun getMd5(str: String): String {
        val bs = md5.digest(str.toByteArray())
        val sb = StringBuilder(40)
        bs.forEach {
            1 shr 4
           val a =  ((it and (0xff).toByte()).toInt() shr (4).toInt())
            if( a == 0) {
            sb.append("0").append(Integer.toHexString((it and (0xff).toByte()).toInt()))
        } else {
            sb.append(Integer.toHexString((it and (0xff).toByte()).toInt()))
        }
        }
        val md5 = StringBuffer(40)
        sb.split("ffffff").forEach {
            md5.append(it)
        }
        return md5.toString()
    }
}

