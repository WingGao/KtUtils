package com.github.winggao.kt.rd

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.Unpooled
import org.redisson.client.codec.BaseCodec
import org.redisson.client.handler.State
import org.redisson.client.protocol.Decoder
import org.redisson.client.protocol.Encoder

/**
 * RedissonClient专用JSON转换器
 * User: Wing
 * Date: 2021/8/20
 */
class TypedFastJsonCodec : BaseCodec {
    var keyClazz: Class<*>? = null
    var valueClazz: Class<*>? = null

    constructor() { //默认支持string
    }

    constructor(valueClazz: Class<*>) {
        this.valueClazz = valueClazz
    }

    constructor(keyClazz: Class<*>, valueClazz: Class<*>) {
        this.keyClazz = keyClazz
        this.valueClazz = valueClazz
    }

    private fun decode1(buf: ByteBuf?, state: State?, clz: Class<*>?): Any? {
        if (buf == null) return null
        val ips = ByteBufInputStream(buf)
        val bya = ByteArray(buf.capacity())
        ips.read(bya)
        if (clz == null || clz == JSONObject::class.java) return JSON.parse(bya)
        return JSON.parseObject(bya, clz)
    }

    private val vDecoder = Decoder<Any?> { buf, state -> decode1(buf, state, valueClazz) }
    private val kDecoder = Decoder<Any?> { buf, state -> decode1(buf, state, keyClazz) }
    val encoder = object : Encoder {
        override fun encode(obj: Any?): ByteBuf? {
            if (obj == null) return null
            var byt: ByteArray
            if (obj is String) byt = obj.toByteArray()
            else byt = JSON.toJSONBytes(obj)
            return Unpooled.copiedBuffer(byt)
        }
    }

    override fun getMapKeyDecoder(): Decoder<Any?> {
        return kDecoder
    }

    override fun getValueDecoder(): Decoder<Any?> {
        return vDecoder
    }

    override fun getValueEncoder(): Encoder {
        return encoder
    }
}
