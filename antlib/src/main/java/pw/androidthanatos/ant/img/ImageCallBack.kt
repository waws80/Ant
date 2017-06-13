package pw.androidthanatos.ant.img

/**
 * Created by liuxiongfei on 2017/6/13.
 */
interface ImageCallBack {

    fun progress(progress: Int)

    fun complate()

    fun error()
}