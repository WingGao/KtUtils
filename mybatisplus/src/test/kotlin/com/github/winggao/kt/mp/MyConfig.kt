package com.github.winggao.kt.mp

import com.github.yulichang.autoconfigure.MybatisPlusJoinAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

@Configuration
@Import(MybatisPlusJoinAutoConfiguration::class)
open class MyConfig {
    @Bean
    open fun dataSource(): DataSource? {
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName("org.sqlite.JDBC")
        dataSource.url = "jdbc:sqlite::memory:hello"
        return dataSource
    }
}