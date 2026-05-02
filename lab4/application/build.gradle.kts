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
    implementation(project(":lab4:domain"))
    implementation(project(":lab4:application.abstractions"))
    implementation(project(":lab4:application.contracts"))
    implementation("org.springframework:spring-context:7.0.6")
    implementation("org.springframework:spring-tx:7.0.6")
    implementation("org.springframework.boot:spring-boot-starter-security:4.1.0-M4")
}

tasks.test {
    useJUnitPlatform()
}