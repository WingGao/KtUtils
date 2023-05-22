package com.github.winggao.kt.mp

import com.baomidou.mybatisplus.annotation.TableLogic
import com.baomidou.mybatisplus.annotation.TableName
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.github.yulichang.base.MPJBaseMapper
import org.apache.ibatis.annotations.Mapper


@TableName("test_table1")
open class TestEntity {
    var id: Long? = null
    var name: String? = null
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

class TestEntityFull : TestEntity() {
    var e2: TestEntity2? = null
}