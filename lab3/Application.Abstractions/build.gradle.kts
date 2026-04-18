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
}

tasks.test {
    useJUnitPlatform()
}