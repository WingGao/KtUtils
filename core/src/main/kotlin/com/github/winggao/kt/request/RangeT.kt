package com.github.winggao.kt.request

import java.util.*

open class RangeT<T> {
    open var begin: T? = null
    open var end: T? = null

    var includeLeft = true
    var includeRight = true


}

open class RangeTR<T, R : RangeTR<T, R>> : RangeT<T>() {
    open fun set(b: T?, e: T?): R {
        this.begin = b
        this.end = e
        return this as R
    }
}

class RangeInt : RangeTR<Int, RangeInt>() {

}

class RangeLong : RangeTR<Long, RangeLong>() {

}

class RangeDate : RangeTR<Date, RangeDate>() {
}
