package com.github.winggao.kt.ppd.dao.mg

import com.alibaba.fastjson.parser.DefaultJSONParser
import com.alibaba.fastjson.parser.ParserConfig
import com.alibaba.fastjson.serializer.JSONSerializer
import com.alibaba.fastjson.serializer.SerializeConfig
import com.alibaba.fastjson.serializer.SerializerFeature
import com.alibaba.fastjson.serializer.StringCodec
import org.bson.types.ObjectId
import java.io.IOException
import java.lang.reflect.Type

/**
 * User: Wing
 * Date: 2021/6/10
 */
open class ObjectIdTypeHandler {
    //fastjson
    companion object {
        @JvmStatic
        fun setFastJson() {
            val imp = FastJsonImpl()
            SerializeConfig.getGlobalInstance().put(ObjectId::class.java, imp)
            ParserConfig.getGlobalInstance().putDeserializer(ObjectId::class.java, imp)
        }
    }

    // ObjectId与hexString互相转换
    class FastJsonImpl : StringCodec() {
        @Throws(IOException::class)
        override fun write(serializer: JSONSerializer, valo: Any?, fieldName: Any, fieldType: Type, features: Int) {
            val out = serializer.out

            if (valo == null) {
                out.writeNull(SerializerFeature.WriteNullBooleanAsFalse)
                return
            }
            val value = valo as ObjectId
            out.writeString(value.toHexString())
        }

        override fun <T : Any?> deserialze(parser: DefaultJSONParser?, clazz: Type?, fieldName: Any?): T {
            val r = super.deserialze<String>(parser, String::class.java, fieldName)
            return ObjectId(r) as T
        }
    }

}
