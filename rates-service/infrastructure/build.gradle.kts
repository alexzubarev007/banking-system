dependencies {
    implementation("tools.jackson.core:jackson-databind")
    implementation(project(":rates-service:application"))
    implementation(project(":shared"))
    implementation("org.springframework.boot:spring-boot-starter-amqp")
}
