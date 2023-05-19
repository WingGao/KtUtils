dependencies {
    api(project(":core"))
    implementation("com.baomidou:mybatis-plus:${Constants.mybatisPlusVersion}")
    implementation("com.github.yulichang:mybatis-plus-join:${Constants.mybatisPlusJoinVersion}")

    //测试
    testImplementation("org.mybatis:mybatis:3.5.6")
    testImplementation("org.xerial:sqlite-jdbc:3.41.2.1")
    testImplementation("com.baomidou:mybatis-plus-boot-starter-test:${Constants.mybatisPlusVersion}")
}