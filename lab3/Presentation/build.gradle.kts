plugins {
    id("java")
    id("io.freefair.lombok") version "9.2.0"
    id("org.springframework.boot") version "4.1.0-M4"
}

group = "ru.Zubarev"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":lab3:Domain"))
    implementation(project(":lab3:Application"))
    implementation(project(":lab3:Application.Contracts"))
    implementation(project(":lab3:Application.Abstractions"))
    implementation(project(":lab3:Infrastructure"))
    implementation("org.springframework.boot:spring-boot-starter-web:4.1.0-M4")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:4.1.0-M4")
    testImplementation("org.springframework.boot:spring-boot-starter-test:4.1.0-M4")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}