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
    implementation(project(":lab2:Application.Abstractions"))
    implementation(project(":lab2:Application.Contracts"))
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-junit-jupiter:5.14.2")
    testImplementation("net.bytebuddy:byte-buddy:1.17.7")
    testImplementation("net.bytebuddy:byte-buddy-agent:1.17.7")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}