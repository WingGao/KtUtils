package com.github.winggao.kt.ppd.dao.mg

import com.baomidou.mybatisplus.core.toolkit.ReflectionKit
import com.github.winggao.kt.request.WPage
import com.github.winggao.kt.request.WPageT
import dev.morphia.Datastore
import dev.morphia.Key
import dev.morphia.query.FindOptions
import dev.morphia.query.Query
import dev.morphia.query.Sort
import dev.morphia.query.UpdateOperations
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.declaredMemberProperties

/**
 * 模仿MybatisPlus的service接口
 * User: Wing
 * Date: 2021/6/9
 * https://morphia.dev/morphia/1.6/index.html
 */

open class WingMgService<T : MongoEntity> {
    val logger = LoggerFactory.getLogger(this.javaClass)

    //    @Autowired
//    lateinit var template: MongoTemplate
    @Autowired
    lateinit var store: Datastore
    val entityClass by lazy {
        currentModelClass()
    }

    val columnMap by lazy {
        entityClass.declaredFields.map {
            it.name to it.name
        }.toMap().toMutableMap().also { m ->
            entityClass.kotlin.allSuperclasses.forEach { sup ->
                sup.declaredMemberProperties.forEach { p ->
//                    val pk = p.toString()
                    m.getOrPut(p.name, { p.name })
                }
            }
        }
    }

    protected open fun currentModelClass(): Class<T> {
        val c = ReflectionKit.getSuperClassGenericType(javaClass, WingMgService::class.java, 0) as Class<T>
        return c
    }

    fun updateOp(): UpdateOperations<T> {
        return store.createUpdateOperations(entityClass)
    }
//    inline fun <reified V : MongoEntity> classOf() = V::class

    fun save(entity: T): Key<T>? {
        if (entity.id == null) entity.createdAt = Date()
        return store.save(entity)
    }

    fun saveBatch(entityList: Collection<T>) {
        entityList.chunked(100).forEach { batch ->
            batch.forEach {
                if (it.id == null) it.createdAt = Date()
            }
            store.save(batch)
        }
    }

    fun updateById(entity: T): Boolean {
        // merge操作不影响null
        val k = store.merge(entity)
        return true
    }

    fun query(): Query<T> {
        return store.createQuery(entityClass)
    }

    fun ktQuery(): WingMgKtQuery<T> {
        return WingMgKtQuery(this)
    }

    fun getById(hexId: String): T? {
        return query().field("_id").equal(ObjectId(hexId)).first()
    }


    fun listPage(q: Query<T>, page: WPage): WPageT<T> {
        val count = q.count()
        if (page.current < 1) page.current = 1
        if (page.size == null || page.size!! <= 0) page.size = 100 //默认分页100
        val skip = (page.current - 1) * page.size!!
        val res = WPageT<T>()
        res.current = page.current
        res.total = count
        res.size = page.size
        if (skip >= count) {
            res.records = ArrayList()
            return res
        }
        if (!page.orders.isNullOrEmpty()) {
            page.orders!!.forEach {
                q.order(if (it.asc) Sort.ascending(it.column) else Sort.descending(it.column))
            }
        }
        val items = q.asList(FindOptions().limit(page.size!!.toInt()).skip(skip.toInt()))
        res.records = items
        return res
    }

    fun createAggregation(): WingAggregationPipeline {
        return WingAggregationPipeline(store, store.getCollection(entityClass), entityClass)
    }
}
