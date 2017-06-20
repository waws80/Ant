package pw.androidthanatos.ant.controller

import pw.androidthanatos.ant.entity.Chain

/**
 * 网络请求拦截器
 */
interface AntInterceptor {

    /**
     * 拦截器
     * @param chain 请求信息实体
     * @return  true 执行此次操作 false 拦截此次操作
     */
    fun interceptor(chain: Chain): Chain
}