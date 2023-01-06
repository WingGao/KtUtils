import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.*

object Constants {
    // kotlin相关
    const val ktSerializationVersion = "1.3.3"

    const val ktCoroutinesVersion = "1.6.4" //https://github.com/Kotlin/kotlinx.coroutines/releases
    const val kotlinVersion = "1.7.21"

    const val springVersion = "1.5.22.RELEASE"

    const val mybatisPlusVersion = "3.5.3.1"
    const val mybatisPlusJoinVersion = "1.4.2" // https://github.com/yulichang/mybatis-plus-join

    const val hutoolVersion = "5.7.8"
    const val fastjsonVersion = "1.2.75"

    const val swagger = "2.9.2"
    const val redissonVersion = "3.17.5"

    //    const val okhttp3Version = "3.14.9"
//    const val httpClientVersion = "4.5.13"
    const val okhttp3Version = "ppd-1.4.7"
    const val okhttp2Version = "ppd-1.4.6"
    const val httpClientVersion = "ppd-4.5.3-11"
    const val ppdMicVersion = "1.1.12"
    const val ppdJobVersion = "1.1.13"

    // Constants.mavenRepos.forEach { val mv = maven(it);mv.isAllowInsecureProtocol = true }
    val mavenRepos = listOf(
        "https://maven.aliyun.com/repository/public",
//        "http://mirrors.cloud.tencent.com/nexus/repository/maven-public/",
        "http://maven.repo.ppdai.com/nexus/content/groups/public/",
        "https://jitpack.io",
        "https://maven.pkg.jetbrains.space/public/",
        "https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers"
    )
}


//共享脚本
fun loadShareLib(scope: DependencyHandlerScope, useApi: Boolean = false) {
    val implementation = { v: Any ->
        if (useApi) scope.add("api", v)
        else scope.add("implementation", v)
    }
    scope.run {
        //kotlin
        implementation(kotlin("stdlib-jdk8"))
        implementation(kotlin("reflect"))
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Constants.ktCoroutinesVersion}")

        implementation("cn.hutool:hutool-all:${Constants.hutoolVersion}")
        implementation("com.alibaba:fastjson:${Constants.fastjsonVersion}")
        implementation("org.slf4j:slf4j-api:1.7.36")
    }
}

fun pathProject(proj: Project) {
    proj.run {
        //添加maven打包
        (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("publishing", object : Action<org.gradle.api.publish.PublishingExtension> {
            override fun execute(t: PublishingExtension) {
                t.run {
                    publications {
                        create<MavenPublication>("maven") {
                            from(components["java"])
                        }
                    }
                }
            }
        })
    }
}