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
    implementation(project(":lab5:rates-service:application"))
    implementation(project(":lab5:rates-service:infrastructure"))
    implementation("org.springframework.boot:spring-boot-starter:4.1.0-M4")
    implementation("tools.jackson.core:jackson-core:3.0.0-rc9")
    implementation("tools.jackson.core:jackson-databind:3.0.0-rc9")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}