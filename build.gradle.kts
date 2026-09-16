plugins {
    base
    id("org.springframework.boot") apply false
}
val springBootVersion: String by project
subprojects {
    apply(plugin = "java-library")
    group = "ru.zubarev.bank"
    version = "1.0.0"
    repositories { mavenCentral() }
    extensions.configure<JavaPluginExtension> { toolchain.languageVersion.set(JavaLanguageVersion.of(25)) }
    dependencies {
        "implementation"(platform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
        "compileOnly"("org.projectlombok:lombok")
        "annotationProcessor"(platform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
        "annotationProcessor"("org.projectlombok:lombok")
        "testImplementation"("org.springframework.boot:spring-boot-starter-test")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    }
    tasks.withType<JavaCompile>().configureEach { options.compilerArgs.add("-parameters") }
    tasks.withType<Test>().configureEach {
        inputs.property("integration", project.hasProperty("integration"))
        useJUnitPlatform { if (!project.hasProperty("integration")) excludeTags("integration") }
    }
}
tasks.named("check") { dependsOn(subprojects.map { "${it.path}:check" }) }
tasks.named("build") { dependsOn(subprojects.map { "${it.path}:build" }) }
