package com.github.winggao.kt.mp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource


@SpringBootApplication(scanBasePackages = ["com.github.winggao.kt"])
@ComponentScan(basePackages = ["com.github.winggao.kt"])
open class MybatisPlusTestApplication {

}