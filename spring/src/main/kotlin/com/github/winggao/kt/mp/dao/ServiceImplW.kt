package com.github.winggao.kt.ppd.dao

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit
import com.baomidou.mybatisplus.extension.kotlin.KtQueryChainWrapper
import com.github.winggao.kt.request.WError
import com.github.yulichang.base.MPJBaseMapper
import com.github.yulichang.base.MPJBaseServiceImpl
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.KProperty

/**
 * User: Wing
 * Date: 2022/9/26
 * 注意 MPJBaseServiceImpl没有逻辑删除！
 */
open class ServiceImplW<M : MPJBaseMapper<T>, T : EntityW> : MPJBaseServiceImpl<M, T>() {
    val logger = LoggerFactory.getLogger(this.javaClass)

    private var mBizKey: TableFieldInfo? = null

    protected val bizKey: TableFieldInfo
        get() {
            if (mBizKey == null) {
                synchronized(this) {
                    val tableInfo = TableInfoHelper.getTableInfo(entityClass)
                    tableInfo.fieldList.forEach { f ->
                        if (f.field.isAnnotationPresent(WingTableBizKey::class.java)) {
                            mBizKey = f
                        }
                    }
                }
            }
            return mBizKey!!
        }

    fun getBizValue(t: T): Any? {
        val v = ReflectionKit.getFieldValue(t, bizKey.property)
        return v
    }

    // 注意！必须是open
    open fun getByBizId(bizId: Any): T? {
        return query().eq(bizKey.column, bizId).one()
    }

    /**
     * 根据bizKey来更新单个记录
     */
    @Transactional(rollbackFor = [Exception::class])
    open fun saveOrUpdateByBizKey(entity: T): Boolean {
        if (entity.id != null) {
            return updateById(entity)
        }

        val old = query().eq(bizKey.column, getBizValue(entity)!!).oneOpt()
        if (old.isPresent) {
            //更新
            entity.id = old.get().id
            return updateById(entity)
        } else {
            entity.id = null
            return save(entity)
        }
    }

    /**
     * 根据bizKey来更新表
     */
    @Transactional(rollbackFor = [Exception::class])
    open fun saveOrUpdateBatchByBizKey(
        entityList: Collection<T>
    ) {
        if (bizKey == null) throw WError("${entityClass.simpleName}未指定WingTableBizKey")
        entityList.chunked(1000).forEach { batch ->
            // 根据key来查id
            val eMap = entityList.associateBy {
                val v = ReflectionKit.getFieldValue(it, bizKey.property)
                if (v == null) throw WError("${bizKey.property}为null")
                v
            }
            //TODO 其实应该用on duplicate key来操作
            val oldMap = query().`in`(bizKey.column, eMap.keys).select("id", bizKey.column).list()
                .associateBy { ReflectionKit.getFieldValue(it, bizKey.property) }
            eMap.forEach { k, v ->
                val old = oldMap[k]
                if (old != null) { //将要更新的id赋值
                    v.id = old.id
                }
            }
            saveOrUpdateBatch(batch)
        }
    }

    /**
     * 补全items的信息
     * @param getId 获得id的方法
     */
    fun <A> fillItemsById(items: Collection<A>, getId: (A) -> Long, onEach: (T, A) -> Unit) {
        if (items.isEmpty()) return
        val itemMap = items.associateBy(getId)
        val oldItems = listByIds(itemMap.keys)
        oldItems.forEach {
            onEach(it, itemMap[it.id]!!)
        }
    }

    fun <A> fillItemsByCol(items: Collection<A>, column: KProperty<*>, getColVal: (A) -> Any, onEach: (T, A) -> Unit) {
        if (items.isEmpty()) return
        val itemMap = items.associateBy(getColVal)
        val oldItems = ktQuery().`in`(column, itemMap.keys).list()
        oldItems.forEach {
            val v = itemMap[column.call(it)]
            if (v != null) onEach(it, v)
        }
    }

    /**
     * 根据keyId来新增或替换原数据
     * 一般多使用 1:N 的关系
     * 注意那些没有逻辑删除的model
     * @param col N代表的字段
     * @param forceReplace 是否优先替换不匹配的为item，需要item全量传入
     * @param queryBuild
     */
    @Transactional
    open fun <A> createOrReplaceBatch(items: Collection<T>, nCol: KProperty<A>, queryBuild: (KtQueryChainWrapper<T>) -> KtQueryChainWrapper<T>, forceReplace: Boolean = false) {
        val iMap = items.associateBy { nCol.call(it) }.toMutableMap()
        val oldList = queryBuild(ktQuery()).list()
        val upList = ArrayList<T>()
        val delList = ArrayList<T>() //要删除的
        oldList.forEach {
            val item = iMap.remove(nCol.call(it))
            if (item == null) {
                delList.add(it)
            } else {
                item.id = it.id
                upList.add(item)
            }
        }
        //剩余的替换
        iMap.forEach {
            val item = it.value
            if (forceReplace && delList.isNotEmpty()) {
                // 强制替换
                val old = delList.removeAt(0)
                item.id = old.id
            } else {
                upList.add(item)
            }
        }
        //删除
        if (delList.isNotEmpty()) removeByIds(delList.map { it.id })
        //保存
        if (upList.isNotEmpty()) saveOrUpdateBatch(upList)
    }
}
