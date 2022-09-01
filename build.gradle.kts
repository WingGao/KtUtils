import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    `maven-publish`
}

group = "com.github.WingGao"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}


// 常用依赖
fun addDep(proj: Project) {
    val addImp = { x: Any -> proj.dependencies.add("implementation", x) }
    //
    addImp("com.alibaba:fastjson:1.2.83")
    //kotlin
    addImp(proj.dependencies.kotlin("reflect"))
}

addDep(project)