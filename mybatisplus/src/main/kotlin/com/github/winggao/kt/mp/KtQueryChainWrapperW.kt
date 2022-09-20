package com.github.winggao.kt.mp

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.extension.kotlin.KtQueryChainWrapper

open class KtQueryChainWrapperW<T : Any>(baseMapper: BaseMapper<T>) : KtQueryChainWrapper<T>(baseMapper) {
    fun select(vararg columns: String): KtQueryChainWrapperW<T> {
        wrapperChildren.select(*columns)
        return this
    }
}
