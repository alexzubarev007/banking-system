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
    implementation(project(":lab2:Domain"))
    implementation(project(":lab2:Application"))
    implementation(project(":lab2:Application.Abstractions"))
    implementation(project(":lab2:Application.Contracts"))
    implementation(project(":lab2:Infrastructure"))
    implementation("org.hibernate:hibernate-core:6.4.0.Final")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}