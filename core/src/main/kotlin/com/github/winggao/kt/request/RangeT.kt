package com.github.winggao.kt.request

import java.util.*

open class RangeT<T> {
    open var begin: T? = null
    open var end: T? = null
    var includeLeft = true
    var includeRight = true
}

class RangeInt : RangeT<Int>() {

}

class RangeLong : RangeT<Long>() {

}

class RangeDate : RangeT<Date>() {
}
