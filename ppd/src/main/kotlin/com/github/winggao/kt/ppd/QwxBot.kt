package com.github.winggao.kt.ppd

import cn.hutool.http.HttpUtil
import com.alibaba.fastjson.JSON
import com.github.winggao.kt.spring.SpringUtilsW
import org.slf4j.LoggerFactory

/**
 * 企业微信Bot
 */
class QwxBot(val botUrl: String, val env: String) {
    val logger = LoggerFactory.getLogger(this.javaClass)

    fun alarm(title: String, msg: String?, log: Boolean? = false, err: Throwable? = null) {
        val sb = StringBuilder("[<font color = \"info\">$env</font>] $title")
        SpringUtilsW.currentHttpServletRequest()?.let { req ->
            sb.append("\n").append("`${req.requestURI}`")
            req.getHeader("")?.let { dt ->
                sb.append("\n").append("谛听ID `${req}`")
            }
        }
        if (!msg.isNullOrEmpty()) sb.append("\n").append(msg)
        if (err != null) sb.append("\n").append(err.stackTraceToString())
        HttpUtil.post(
            botUrl, JSON.toJSONString(
                mapOf(
                    "msgtype" to "markdown",
                    "markdown" to mapOf(
                        "content" to "[<font color = \"info\">$env</font>] $title\n${msg ?: ""}"
                    )
                )
            )
        )
    }
}
