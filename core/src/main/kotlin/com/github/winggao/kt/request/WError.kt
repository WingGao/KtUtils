package com.github.winggao.kt.request

/**
 * 业务错误
 * User: Wing
 * Date: 2021/4/15
 */
open class WError(var code: Int, message: String) : RuntimeException(message) {
    var rawErr: Exception? = null
    var errorMsg: String = message

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WError

        if (code != other.code) return false
        if (errorMsg != other.errorMsg) return false

        return true
    }

    fun clone(): WError {
        return WError(this).also {
            it.code = code
            it.errorMsg = errorMsg
        }
    }

    constructor(message2: String) : this(-1, message2)

    constructor(e: Exception) : this(-1, e.localizedMessage ?: "未知") {
        rawErr = e
        stackTrace = e.stackTrace
    }
}
