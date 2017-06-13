package pw.androidthanatos.ant.entity

import pw.androidthanatos.ant.controller.AntMethod

/**
 * 请求信息实体类
 */

data class Chain(var url: String,
                 var method: AntMethod,
                 var headers: HashMap<String,String>,
                 var body: String)
