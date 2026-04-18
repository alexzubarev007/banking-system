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
    implementation(project(":lab3:Domain"))
    implementation(project(":lab3:Application.Abstractions"))
    implementation(project(":lab3:Application.Contracts"))
    implementation("org.springframework:spring-context:7.0.6")
    implementation("org.springframework:spring-tx:7.0.6")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}