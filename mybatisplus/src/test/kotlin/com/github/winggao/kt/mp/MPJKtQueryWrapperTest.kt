package com.github.winggao.kt.mp

import com.baomidou.mybatisplus.annotation.TableName
import com.baomidou.mybatisplus.core.MybatisMapperAnnotationBuilder
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest
import com.github.yulichang.autoconfigure.MybatisPlusJoinAutoConfiguration
import com.github.yulichang.autoconfigure.MybatisPlusJoinProperties
import org.apache.ibatis.mapping.Environment
import org.apache.ibatis.session.Configuration
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.apache.ibatis.transaction.TransactionFactory
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import java.io.PrintWriter
import java.sql.DriverManager
import javax.sql.DataSource


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@MybatisPlusTest
open class MPJKtQueryWrapperTest {
    fun setup1() {
        val jc = DriverManager.getConnection("jdbc:sqlite::memory:")
        val dataSource: DataSource = object : DataSource {
            var pw = PrintWriter(System.out)
            override fun getConnection(): java.sql.Connection {
                return jc
            }

            override fun getConnection(username: String?, password: String?): java.sql.Connection {
                return jc
            }

            override fun isWrapperFor(iface: Class<*>?): Boolean {
                return true
            }

            override fun getLogWriter(): PrintWriter {
                return pw
            }

            override fun setLogWriter(out: PrintWriter?) {
                if (out != null) pw = out
            }

            override fun setLoginTimeout(seconds: Int) {
            }

            override fun getLoginTimeout(): Int {
                return 0
            }

            override fun getParentLogger(): java.util.logging.Logger {
                return java.util.logging.Logger.getLogger("test")
            }

            override fun <T : Any?> unwrap(iface: Class<T>?): T {
                TODO("Not yet implemented")
            }
        }
        val transactionFactory: TransactionFactory = JdbcTransactionFactory()
        val environment = Environment("development", transactionFactory, dataSource)
        val configuration = Configuration(environment)
//        configuration.addMapper(TestEntity2Mapper::class.java)
        val mapperBuilderAssistant = MybatisMapperAnnotationBuilder(configuration, TestEntity2Mapper::class.java)
        mapperBuilderAssistant.parse()
        val sqlSessionFactory: SqlSessionFactory = SqlSessionFactoryBuilder().build(configuration)
        val mpc = MybatisPlusJoinAutoConfiguration(MybatisPlusJoinProperties())
        mpc.interceptorConfig(listOf(sqlSessionFactory))
        val session = sqlSessionFactory.openSession()
//        session.getMapper(TestEntity2Mapper::class.java).insert(TestEntity2().apply {
//            id = 1L
//            name = "test"
//        })
    }

    @Autowired
    lateinit var t1Mapper: TestEntity1Mapper

    @Test
    fun testLeftJoin() {
//        setup()
        val ktQ = MPJKtQueryWrapper(TestEntity::class).leftJoin(TestEntity2::class, onMS = {
            it.eq(TestEntity::id, TestEntity2::id)
        }).leftJoin(TestEntity3::class, onMS = {
            it.eq(TestEntity::id, TestEntity3::id).ge(TestEntity2::id, 2L)
                .`in`(TestEntity3::id, listOf(2L, 3L))
        }).where {
            it.eq(TestEntity::id, 1L)
        }
            .ktSelect(TestEntityFull::class)
            .ktSelect(TestEntityFull::e4, TestEntity3::title3)
            .groupBy(TestEntity::id)
//        t1Mapper.selectJoinList(TestEntityFull::class.java, ktQ)
        println(ktQ.sqlSelect)
    }
}

