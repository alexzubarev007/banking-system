plugins {
    id("java")
    id("io.freefair.lombok") version "9.2.0"
}

group = "ru.Zubarev"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}