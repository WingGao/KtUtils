package com.github.winggao.kt.mp

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.extension.kotlin.KtQueryChainWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl

open class KtQueryChainWrapperW<T : Any>(baseMapper: BaseMapper<T>, entityClass: Class<T>) : KtQueryChainWrapper<T>(baseMapper, entityClass) {
    fun select(vararg columns: String): KtQueryChainWrapperW<T> {
        wrapperChildren.select(*columns)
        return this
    }
}

fun <M : BaseMapper<T>, T : Any> ServiceImpl<M, T>.ktQueryW(): KtQueryChainWrapperW<T> {
    return KtQueryChainWrapperW(baseMapper, entityClass)
}