package com.github.winggao.kt.mp

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableLogic
import com.baomidou.mybatisplus.annotation.TableName
import com.github.yulichang.base.MPJBaseMapper
import org.apache.ibatis.annotations.Mapper


@TableName("test_table1")
open class TestEntity {
    var id: Long? = null
    var name: String? = null

    @TableField(exist = false)
    var hide: Int? = null
}

@Mapper
interface TestEntity1Mapper : MPJBaseMapper<TestEntity> {

}

@TableName("test_table2")
class TestEntity2 {
    var id: Long? = null
    var name: String? = null

    @TableLogic(value = "1", delval = "0")
    var isactive: Boolean? = null
}

@Mapper
interface TestEntity2Mapper : MPJBaseMapper<TestEntity2> {

}

@TableName("test_table3")
class TestEntity3 {
    var id: Long? = null
    var title3: String? = null

    @TableLogic(value = "1", delval = "0")
    var isactive: Boolean? = null
}

@Mapper
interface TestEntity3Mapper : MPJBaseMapper<TestEntity3> {

}

class TestEntityFull : TestEntity() {
    var e2: TestEntity2? = null

    @MPJResultFieldJ(table = TestEntity3::class)
    @MPJResultField(TestEntity3::class)
    var e3: E3? = null

    var e4: String? = null

    var tableAlias: String? = null


    class E3 {
        var title3: String? = null
    }
}