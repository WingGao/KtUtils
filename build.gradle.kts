import org.gradle.kotlin.dsl.*;
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val ktVersion = Constants.kotlinVersion
    id("org.jetbrains.kotlin.plugin.spring") version ktVersion apply false
    kotlin("jvm") version ktVersion apply false
    kotlin("plugin.serialization") version ktVersion apply false
    kotlin("plugin.allopen") version ktVersion apply false
}

repositories {
    mavenLocal()
    Constants.mavenRepos.forEach { val mv = maven(it);mv.isAllowInsecureProtocol = true }
}

subprojects {
    group = "com.github.WingGao"

    repositories {
        mavenLocal()
        Constants.mavenRepos.forEach { val mv = maven(it);mv.isAllowInsecureProtocol = true }
    }

    apply {
        plugin("kotlin")
        plugin("maven-publish")
    }

    dependencies {
        loadShareLib(this)
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }


    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
        }
    }
}