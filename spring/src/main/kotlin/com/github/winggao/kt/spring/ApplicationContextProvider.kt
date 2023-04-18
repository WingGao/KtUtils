package com.github.winggao.kt.spring

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

@Component
class ApplicationContextProvider : ApplicationContextAware {
    override fun setApplicationContext(ac: ApplicationContext) {
        applicationContext = ac
    }

    companion object {
        @JvmStatic
        fun getApplicationContext(): ApplicationContext {
            return applicationContext!!
        }

        private var applicationContext: ApplicationContext? = null

        @JvmStatic
        fun <T : Any> getBean(clazz: Class<T>): T {
            return getApplicationContext().getBean(clazz)
        }
    }
}