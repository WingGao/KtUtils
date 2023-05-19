package com.github.winggao.kt.mp

import com.baomidou.mybatisplus.annotation.TableName
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper


@TableName("test_table1")
open class TestEntity {
    var id: Long? = null
    var name: String? = null
}

@TableName("test_table2")
class TestEntity2 {
    var id: Long? = null
    var name: String? = null
}

@Mapper
interface TestEntity2Mapper : BaseMapper<TestEntity2> {

}

class TestEntityFull : TestEntity() {
    var e2: TestEntity2? = null
}