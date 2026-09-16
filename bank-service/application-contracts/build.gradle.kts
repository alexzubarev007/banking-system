plugins {
    id("java")
}

group = "ru.Zubarev"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":lab5:bank-service:domain"))
    implementation("io.swagger.core.v3:swagger-annotations:2.2.9")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3")
}

tasks.test {
    useJUnitPlatform()
}