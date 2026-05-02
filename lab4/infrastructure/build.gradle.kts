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
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:4.1.0-M4")
    implementation("org.springframework.boot:spring-boot-starter-security:4.1.0-M4")
    runtimeOnly("org.postgresql:postgresql:42.7.5")
}

tasks.test {
    useJUnitPlatform()
}