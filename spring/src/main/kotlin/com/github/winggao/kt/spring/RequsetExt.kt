package com.github.winggao.kt.spring

import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import javax.servlet.http.HttpServletRequest


object SpringUtilsW {
    fun currentHttpServletRequest(): HttpServletRequest? {
        return kotlin.runCatching {
            (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request
        }.getOrNull()
    }
}

