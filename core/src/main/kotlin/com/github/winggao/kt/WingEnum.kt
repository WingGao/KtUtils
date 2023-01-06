package com.github.winggao.kt

import kotlin.reflect.KProperty
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType

/**
 * 通用枚举类型
 * User: Wing
 * Date: 2021/12/22
 */
open class WingEnum<T>(
    var key: T,
    var title: String
) {
}

class WingEnumUtil<T, A : WingEnum<T>> {
    private val keyMap = HashMap<T, A>()

    constructor(c: Any) {
        val cc = c::class
        cc.members.forEach {
//            if (it.returnType.classifier == DeviceTypeEnum::class) {
//                val a= 0
//            }
            if (it is KProperty && it.returnType.isSubtypeOf(WingEnum::class.starProjectedType)) {
                val cv = it.call(c) as A
                if (!keyMap.containsKey(cv.key)) keyMap[cv.key] = cv
            }
        }
    }


    fun getKeyMap(): Map<T, A> {
        return keyMap
    }

    fun get(key: T): A? {
        return keyMap[key]
    }
}
