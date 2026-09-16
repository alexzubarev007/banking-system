dependencies {
    implementation("tools.jackson.core:jackson-databind")
    api(project(":bank-service:application-abstractions"))
    implementation(project(":shared"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    runtimeOnly("org.postgresql:postgresql")
}
