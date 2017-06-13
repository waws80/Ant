package pw.androidthanatos.ant.controller

import pw.androidthanatos.ant.Ant


/**
 * 获取到网络数据后向主线程发送数据的回调接口
 */
abstract class ResponseListener<in T> {

   open fun progress(progress: Int){
        Ant.antLog("进度： $progress")
   }

    abstract fun complate(t: T)

    abstract fun failer(errorCode: Int)
}