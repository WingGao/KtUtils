package com.github.winggao.kt.mp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource


@SpringBootApplication()
open class MybatisPlusTestApplication {

}

@Configuration
open class Config {
    @Bean
    open fun dataSource(): DataSource? {
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName("org.sqlite.JDBC")
        dataSource.url = "jdbc:sqlite::memory:hello"
        return dataSource
    }
}