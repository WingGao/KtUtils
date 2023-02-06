package com.github.winggao.kt.spring

import cn.hutool.core.io.IoUtil
import cn.hutool.core.util.ReferenceUtil
import cn.hutool.core.util.ReflectUtil
import org.apache.catalina.connector.CoyoteInputStream
import org.slf4j.LoggerFactory
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.lang.reflect.Field
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.ReadListener
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper
import javax.servlet.http.HttpServletResponse

/**
 * 打印请求信息
 */
object RequestLoggerW {
    val logger = LoggerFactory.getLogger(this.javaClass)

    /**
     * 创建打印filter
     * @param logHeaders 打印header，emptyList(): 表示全部，listOf(x)表示只打印x
     */
    fun createFilter(
        ctxFilter: (HttpServletRequestWrapper, HttpServletResponse) -> Boolean,
        contentLengthLimit: Int = 500,
        logHeaders: List<String>? = null
    ): OncePerRequestFilter {
        return object : OncePerRequestFilter() {
            override fun doFilterInternal(sReq: HttpServletRequest, sResp: HttpServletResponse, p2: FilterChain) {
                val req = if (sReq is ContentCachingRequestWrapper) sReq else ContentCachingRequestWrapper(sReq)
                val start = Date()
                val needLog = ctxFilter(req, sResp)
                if (needLog) {
                    val sb = StringBuilder("Request: ${req.method.uppercase()} ${req.requestURI} contentLength=${req.contentLength} start")
                    if (logHeaders != null) {
                        sb.append("\nHeaders: ")
                        if (logHeaders.isEmpty()) { //打印全部header
                            req.headerNames.asSequence().forEach { sb.append(it, "=", req.getHeader(it), " ‖ ") }
                        } else {
                            logHeaders.forEach { val v = req.getHeader(it); if (!v.isNullOrEmpty()) sb.append(it, "=", v, " ‖ ") }
                        }
                    }
                    // 获取请求
                    req.queryString?.let { if (it.isNotEmpty()) sb.append("\nQuery: ", it) }
                    if (req.contentLength > 0 && req.contentLength <= contentLengthLimit) {
                        req.getParameter("_") //提前解析body
                        sb.append("\nBody: ", req.contentAsByteArray.decodeToString().replace("\n"," "), req.reader.readText())
                        req.reader.mark(0)
                        req.reader.reset()
                        // 重置inputStream
                        val cIps = ReflectUtil.getFieldValue(req, "cachedContent") as ByteArrayOutputStream
                        val myServletInputStream = object : ServletInputStream() {
                            val ips = ByteArrayInputStream(cIps.toByteArray())
                            private var isEnd = false
                            override fun read(): Int {
                                return ips.read().also {
                                    isEnd = it == -1
                                }
                            }

                            override fun isFinished(): Boolean {
                                return isEnd
                            }

                            override fun isReady(): Boolean {
                                return true
                            }

                            override fun setReadListener(listener: ReadListener?) {
                            }
                        }
                        ReflectUtil.setFieldValue(req, "inputStream", myServletInputStream)
                    }
                    logger.info(sb.toString())
                    val rep = if (sResp is ContentCachingResponseWrapper) sResp else ContentCachingResponseWrapper(sResp)
                    p2.doFilter(req, rep)
                    logger.info("Response: ${req.requestURI} ${sResp.status} end duration=${Date().time - start.time}ms\nbody: ${rep.contentAsByteArray.decodeToString()}")
                    rep.copyBodyToResponse()
                } else {
                    p2.doFilter(req, sResp)
                }
            }
        }
    }
}

class ContentCachingRequestWrapperW(request: HttpServletRequest) : ContentCachingRequestWrapper(request) {
    //    private val reader: BufferedReader? = null
    private val superInputStream: ServletInputStream = super.getInputStream()
    private var myInputStream: ByteArrayInputStream? = null
    private val cachedContent by lazy {
        val f = ReflectUtil.getField(ContentCachingRequestWrapper::class.java, "cachedContent")
        ReflectUtil.getFieldValue(this, f) as ByteArrayOutputStream
    }

    override fun getInputStream(): ServletInputStream {
        if (myInputStream == null) {
            IoUtil.read(superInputStream) //这样会填充到cachedContent
            myInputStream = ByteArrayInputStream(cachedContent.toByteArray())
        }
//        return myInputStream as ServletInputStream
        return object : ServletInputStream() {
            private var isEnd = false
            private val ips = myInputStream!!
            override fun read(): Int {
                return ips.read().also {
                    isEnd = it == -1
                }
            }

            override fun isFinished(): Boolean {
                return isEnd
            }

            override fun isReady(): Boolean {
                return true
            }

            override fun setReadListener(listener: ReadListener?) {
            }

            override fun markSupported(): Boolean {
                return ips.markSupported()
            }

            override fun mark(readlimit: Int) {
                return ips.mark(readlimit)
            }

            override fun reset() {
                return ips.reset()
            }
        }
    }

}