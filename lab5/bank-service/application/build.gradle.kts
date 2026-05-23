plugins {
    id("java")
    id("io.freefair.lombok") version "9.2.0"
}

group = "ru.Zubarev"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":lab5:bank-service:domain"))
    implementation(project(":lab5:bank-service:application.abstractions"))
    implementation(project(":lab5:bank-service:application.contracts"))
    implementation(project(":lab5:bank-service:infrastructure"))
    implementation(project(":lab5:shared"))
    implementation("org.springframework:spring-context:7.0.6")
    implementation("org.springframework:spring-tx:7.0.6")
    implementation("org.springframework.boot:spring-boot-starter-amqp:4.1.0-M4")
    implementation("org.springframework.boot:spring-boot-starter-security:4.1.0-M4")
}

tasks.test {
    useJUnitPlatform()
}