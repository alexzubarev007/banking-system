plugins {
    id("java")
}

group = "ru.Zubarev"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":lab3:Domain"))
    implementation("io.swagger.core.v3:swagger-annotations:2.2.9")
}

tasks.test {
    useJUnitPlatform()
}