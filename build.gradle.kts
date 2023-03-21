import org.gradle.kotlin.dsl.*;
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

plugins {
    val ktVersion = Constants.kotlinVersion
    id("org.jetbrains.kotlin.plugin.spring") version ktVersion apply false
    `java-library`
    kotlin("jvm") version ktVersion apply false
    kotlin("plugin.serialization") version ktVersion apply false
    kotlin("plugin.allopen") version ktVersion apply false
    id("signing")
}

repositories {
    mavenLocal()
    Constants.mavenRepos.forEach { val mv = maven(it);mv.isAllowInsecureProtocol = true }
}

subprojects {
    group = "com.github.WingGao.KtUtils"
    version = "0.0.10-SNAPSHOT"

    repositories {
        mavenLocal()
        Constants.mavenRepos.forEach { val mv = maven(it);mv.isAllowInsecureProtocol = true }
    }

    apply {
        plugin("kotlin")
        plugin("maven-publish")
        plugin("signing")
    }

    dependencies {
        testImplementation(kotlin("test"))
        testImplementation(kotlin("test-junit5"))
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
        loadShareLib(this)
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
        test {
            useJUnitPlatform()
        }
        register("copyLocal") { //将各个脚本复制到build/wjar
            dependsOn("assemble")
            doLast {
                val outDir = file("../build/wjar")
                println("copyLocal ==> $outDir")
                copy {
                    from(files("build/libs/"))
                    into(outDir)
                }
            }
        }
    }


    configure<PublishingExtension> {
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())

        repositories {
            maven {
                isAllowInsecureProtocol = true
                url = uri(properties.getOrDefault("repo", "") as String)
                credentials {
                    username = properties.get("mUser") as String?
                    password = properties.get("mPwd") as String?
                }
            }
        }
        publications {
            create<MavenPublication>("ppd") {
                from(components["java"])
            }
        }
    }
    configure<SigningExtension> {
//        signing {
//            sign(publishing.publications["maven"])
//        }
    }
}

