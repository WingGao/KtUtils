package com.github.winggao.kt.ppd.dao

import com.alibaba.fastjson.annotation.JSONCreator
import com.alibaba.fastjson.annotation.JSONField
import com.baomidou.mybatisplus.annotation.*
import java.io.Serializable
import java.util.*

/**
 * 基础数据库对象
 */
open class EntityW : Serializable {
    //!!重要 修复fastjson无法反序列化
    @JSONCreator
    constructor() {

    }

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    open var id: Long? = null

    /**
     * 插入时间
     */
    open var inserttime: Date? = null

    /**
     * 更新时间
     * 忽略更新，使用mysql的自动更新策略
     */
    @TableField(value = "updatetime", updateStrategy = FieldStrategy.NEVER, insertStrategy = FieldStrategy.NEVER)
    var updatetime: Date? = null

    /**
     * 逻辑删除(1:保留,0:删除)
     */
    @TableLogic
    @JSONField(serialize = false)
    var isactive: Boolean? = null

    fun fromJson(s: String): Any {
        TODO("Not yet implemented")
    }

    /**
     * 清空继续信息
     */
    fun removeDbBase() {
        isactive = null
        inserttime = null
        updatetime = null
    }
}