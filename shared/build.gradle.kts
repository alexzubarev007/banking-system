plugins {
    id("java")
}

group = "ru.Zubarev"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
tasks.test {
    useJUnitPlatform()
}