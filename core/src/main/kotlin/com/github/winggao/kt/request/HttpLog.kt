package com.github.winggao.kt.request

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.util.ReflectUtil
import cn.hutool.core.util.StrUtil
import cn.hutool.http.*
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import org.slf4j.Logger

/**
 * 包装一下hutool.HttpUtil 使其带有log
 * User: Wing
 * Date: 2022/1/14
 */
class HttpLog(val logger: Logger?) {
    companion object {
        private val bodyMethod by lazy {
            ReflectUtil.getField(HttpBase::class.java, "bodyBytes").also { it.setAccessible(true) }
        }

        fun fmtRequest(req: HttpBase<*>): String {
            var isReq = true
            var prefix = "Request"
            val sb = StrUtil.builder()
            if (req is HttpRequest) {
                sb.append("Request Url: ").append("[").append(req.method).append("] ").append(req.url)
                    .append(StrUtil.CRLF)
            } else {
                prefix = "Response"
                isReq = false
            }
            sb.append("$prefix Headers: ").append(StrUtil.CRLF)
            val indStr = "    "
            req.headers().forEach { (key, value) ->
                sb.append(indStr).append(key).append(": ").append(CollUtil.join(value, ",")).append(StrUtil.CRLF)
            }
            sb.append("$prefix Body: ").append(StrUtil.CRLF)
            sb.append(indStr)
            if (isReq) { //请求
                val r = req as HttpRequest
                when (r.method) {
                    Method.GET -> {

                    }

                    else -> {
                        if (!req.form().isNullOrEmpty()) {
                            sb.append(HttpUtil.toParams(req.form(), req.charset()))
                        } else {
                            val rb = bodyMethod.get(req) as ByteArray?
                            sb.append(StrUtil.str(rb, req.charset()))
                        }
                    }
                }
            } else { //返回
                val r = req as HttpResponse
                val cType = r.header(Header.CONTENT_TYPE)
                if (cType != null && cType.contains(Regex("json|html|text|xml"))) {
                    sb.append(r.body())
                } else {
                    sb.append("*二进制${r.header(Header.CONTENT_LENGTH)}*")

                }
            }
            sb.append(StrUtil.CRLF)

            return sb.toString()
        }
    }

    fun execute(reqBuilder: () -> HttpRequest): HttpResponse {
        val req = reqBuilder()
        val rep = req.execute()
        logger?.info("${fmtRequest(req)}\n${fmtRequest(rep)}")
        return rep
    }

    fun send(
        method: Method,
        url: String,
        paramMap: Map<String, Any?>? = null,
        jsonBody: Any? = null,
        headers: Map<String, String>? = null,
        timeout: Int = HttpGlobalConfig.getTimeout()
    ): HttpResponse {
        val req = HttpRequest(url).method(method);
        if (paramMap != null) req.form(paramMap)
        if (jsonBody != null) {
            when (jsonBody) {
                is String -> req.body(jsonBody)
                is ByteArray -> req.body(jsonBody)
                else -> req.body(JSON.toJSONString(jsonBody))
            }
            req.contentType(ContentType.JSON.value)
        }
        if (!headers.isNullOrEmpty()) req.addHeaders(headers)
        val rep = req.execute()
        logger?.info("${fmtRequest(req)}\n${fmtRequest(rep)}")
        return rep
    }


    fun post(urlString: String, paramMap: Map<String, Any?>): String {
        return send(Method.POST, urlString, paramMap = paramMap).body()
    }

    fun <T> postJson(urlString: String, paramMap: Map<String, Any?>, outClz: Class<T>): T? {
        val rep = post(urlString, paramMap)
        return JSON.parseObject(rep, outClz)
    }

    fun getBytes(urlString: String, paramMap: Map<String, Any?>? = null): ByteArray {
        return send(Method.GET, urlString, paramMap = paramMap).bodyBytes()
    }

    fun get(urlString: String, paramMap: Map<String, Any?>? = null): String {
        return send(Method.GET, urlString, paramMap = paramMap).body()
    }
}

fun <T> HttpResponse.parseJSON(outClz: Class<T>): T {
    return JSON.parseObject(this.bodyBytes(), outClz)
}
