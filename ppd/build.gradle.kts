dependencies {
    api(project(":core"))
    api(project(":mybatisplus"))

    //spring
    implementation("org.springframework.boot:spring-boot-starter:${Constants.springVersion}")
    implementation("org.springframework.boot:spring-boot-starter-jdbc:${Constants.springVersion}")

    //mybatis
    implementation("com.baomidou:mybatis-plus:${Constants.mybatisPlusVersion}")
    implementation("com.github.yulichang:mybatis-plus-join:${Constants.mybatisPlusJoinVersion}")
    //mongo
    implementation("dev.morphia.morphia:core:1.6.1")
}