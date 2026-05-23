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
    implementation(project(":lab5:bank-service:domain"))
    implementation(project(":lab5:bank-service:application"))
    implementation(project(":lab5:bank-service:application.contracts"))
    implementation(project(":lab5:bank-service:application.abstractions"))
    implementation(project(":lab5:bank-service:infrastructure"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:4.1.0-M4")
    implementation("org.springframework.boot:spring-boot-starter-security:4.1.0-M4")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test:4.1.0-M4")
    implementation("org.springframework.boot:spring-boot-starter-webmvc:4.1.0-M4")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}