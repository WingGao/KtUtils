dependencies {
    api(project(":core"))
    implementation("org.redisson:redisson:${Constants.redissonVersion}") //redis队列
}