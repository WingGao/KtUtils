dependencies {
    api(project(":core"))
    api(project(":mybatisplus"))

    //spring
    implementation("org.springframework.boot:spring-boot-starter:${Constants.springVersion}")
    implementation("org.springframework.boot:spring-boot-starter-web:${Constants.springVersion}")
    implementation("org.springframework.boot:spring-boot-starter-jdbc:${Constants.springVersion}")
    // swagger2 因为spring版本太旧，没法使用新版
    implementation("io.springfox:springfox-swagger2:${Constants.swagger}")
    implementation("io.springfox:springfox-swagger-ui:${Constants.swagger}")
    //mybatis
    implementation("com.baomidou:mybatis-plus:${Constants.mybatisPlusVersion}")
    implementation("com.github.yulichang:mybatis-plus-join:${Constants.mybatisPlusJoinVersion}")
    //mongo
    implementation("dev.morphia.morphia:core:1.6.1")
}