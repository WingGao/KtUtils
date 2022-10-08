package com.github.winggao.kt.ppd.dao.mg

import com.mongodb.DBObject
import dev.morphia.annotations.Id
import dev.morphia.annotations.PrePersist
import org.bson.types.ObjectId
import java.util.*

/**
 * User: Wing
 * Date: 2021/6/9
 */
open class MongoEntity {
    @Id
    var id: ObjectId? = null
    var createdAt: Date? = null
    open var updatedAt: Date? = null

    @PrePersist
    open fun prePersist(retObj: DBObject): DBObject {
        updatedAt = Date()
        return retObj
    }
}
