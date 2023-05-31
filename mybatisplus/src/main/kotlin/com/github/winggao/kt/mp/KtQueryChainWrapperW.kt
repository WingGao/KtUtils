package com.github.winggao.kt.mp

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.extension.kotlin.KtQueryChainWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import kotlin.reflect.KProperty

open class KtQueryChainWrapperW<T : Any>(baseMapper: BaseMapper<T>, entityClass: Class<T>) :
    KtQueryChainWrapper<T>(baseMapper, entityClass) {
    /**
     * 需要放在正常select之后
     */
    fun select(vararg columns: String): KtQueryChainWrapperW<T> {
        wrapperChildren.addSelect(*columns)
        return this
    }

    override fun select(vararg columns: KProperty<*>): KtQueryChainWrapperW<T> {
        super.select(*columns)
        return this
    }
}

fun <M : BaseMapper<T>, T : Any> ServiceImpl<M, T>.ktQueryW(): KtQueryChainWrapperW<T> {
    return KtQueryChainWrapperW(baseMapper, entityClass)
}