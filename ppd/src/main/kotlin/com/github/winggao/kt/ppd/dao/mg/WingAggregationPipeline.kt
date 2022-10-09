package com.github.winggao.kt.ppd.dao.mg

import com.mongodb.AggregationOptions
import com.mongodb.BasicDBList
import com.mongodb.BasicDBObject
import com.mongodb.DBCollection
import dev.morphia.Datastore
import dev.morphia.DatastoreImpl
import dev.morphia.aggregation.AggregationPipeline
import dev.morphia.aggregation.AggregationPipelineImpl

class WingAggregationPipeline(datastore: Datastore, collection: DBCollection, source: Class<*>) : AggregationPipelineImpl(datastore as DatastoreImpl, collection, source) {
    //    fun unset(vararg fields: String): WingAggregationPipeline {
//        return (this as AggregationPipeline).unset(*fields) as WingAggregationPipeline
//    }
    override fun <U : Any?> out(collectionName: String?, target: Class<U>?, options: AggregationOptions?): MutableIterator<U> {
        throw Error("不允许使用\$out")
    }
}


/**
 * $unset mongo>=4.2
 */
fun AggregationPipeline.unset(vararg fields: String): AggregationPipeline {
    return (this as AggregationPipelineImpl).also { p ->
        val unset = BasicDBList().also {
            it.addAll(fields)
        }
        p.stages.add(BasicDBObject("\$unset", unset))
    }
}